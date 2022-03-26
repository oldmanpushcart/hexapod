package io.github.oldmanpushcart.hexapod.thing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.athingx.athing.aliyun.thing.ThingBuilder;
import io.github.athingx.athing.aliyun.thing.ThingOption;
import io.github.athingx.athing.aliyun.thing.container.ThingBootURLLoader;
import io.github.athingx.athing.aliyun.thing.runtime.ThingRuntime;
import io.github.athingx.athing.aliyun.thing.runtime.access.ThingAccess;
import io.github.athingx.athing.aliyun.thing.runtime.specs.DumpTo;
import io.github.athingx.athing.standard.component.ThingCom;
import io.github.athingx.athing.standard.thing.*;
import io.github.athingx.athing.standard.thing.boot.ThInject;
import io.github.athingx.athing.standard.thing.boot.ThingBootArgument;
import io.github.athingx.athing.standard.thing.op.executor.ThingExecutor;
import io.github.athingx.athing.thing.io.ThingIoCom;
import io.github.athingx.athing.thing.io.channel.SerialChannel;
import io.github.athingx.athing.thing.io.source.SerialSource;
import io.github.oldmanpushcart.hexapod.thing.component.HexPostureComp;
import io.github.oldmanpushcart.hexapod.thing.manager.PostureMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.ServoMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.impl.DefaultPostureMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.impl.DefaultServoMgr;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.hexapod.thing.component.HexMonitorComp;
import io.github.oldmanpushcart.hexapod.thing.manager.InfoMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.impl.DefaultInfoMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.channels.ByteChannel;
import java.util.concurrent.Executor;

/**
 * 六足机器人
 */
public class Hexapod {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final String _string;

    /**
     * 六足机器人
     *
     * @param remote 服务器地址
     * @param access 设备访问
     * @param option 设备选项
     * @throws ThingException 构建失败
     */
    public Hexapod(URI remote, ThingAccess access, ThingOption option) throws ThingException {
        this._string = "hexapod:/%s/%s".formatted(access.getProductId(), access.getThingId());
        this.thing = new ThingBuilder(remote, access)
                .option(option)
                .load(new ThingBootURLLoader("/libs/thing-io-raspberry-pi-1.0.0-SNAPSHOT-jar-with-dependencies.jar", prop -> ThingBootArgument.empty()))
                .load(new ThingBootURLLoader("/libs/thing-monitor-general-1.0.0-SNAPSHOT-jar-with-dependencies.jar", prop -> ThingBootArgument.empty()))
                .load(new ThingBootURLLoader("/libs/thing-tunnel-aliyun-1.0.0-SNAPSHOT-jar-with-dependencies.jar", prop -> ThingBootArgument.parse("threads=1&connect.remote=wss%3A%2F%2Fbackend-iotx-remote-debug.aliyun.com%3A443&connect.connect_timeout_ms=10000&connect.handshake_timeout_ms=10000&connect.ping_interval_ms=30000&connect.reconnect_interval_ms=30000&connect.idle_duration_ms=900000&service.local_ssh.type=SSH&service.local_ssh.ip=127.0.0.1&service.local_ssh.port=22&service.local_ssh.option.connect_timeout_ms=10000")))
                .load(new HexPostureComp())
                .load(new HexMonitorComp())
                .load(new ThingLifeCycle() {

                    @ThInject
                    private ThingIoCom thingIo;

                    @Override
                    public void onLoaded(Thing thing) throws Exception {

                        final SerialChannel channel = thingIo.openChannel(SerialSource.n81("/dev/serial0", 9600));
                        final ThingExecutor executor = thing.getThingOp().getThingExecutor();

                        final Injector injector = Guice.createInjector(binder -> {
                            binder.bind(ByteChannel.class).toInstance(channel);
                            binder.bind(Executor.class).toInstance(executor);
                            binder.bind(ServoMgr.class).to(DefaultServoMgr.class);
                            binder.bind(PostureMgr.class).to(DefaultPostureMgr.class);
                            binder.bind(InfoMgr.class).toInstance(new DefaultInfoMgr(_string));
                        });

                        // 注入依赖属性
                        thing.getThingComSet(ThingCom.class).forEach(injector::injectMembers);

                    }

                })
                .build();
    }

    private void _connect(Thing thing, boolean isSyncConnect, boolean isReconnect, long reConnectIntervalMs) {

        // 重连之前先检查是否已被销毁
        if (thing.isDestroyed()) {
            return;
        }

        // 如果是重连，则首先进行等待
        if (isReconnect) {
            try {
                Thread.sleep(reConnectIntervalMs);
            } catch (InterruptedException iCause) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // 尝试连接
        final ThingFuture<ThingConnection> connF = thing.connect()
                .onFailure(e -> _connect(thing, false, true, reConnectIntervalMs))
                .onSuccess(v -> v.getDisconnectFuture().onDone(disconnectF -> _connect(thing, false, true, reConnectIntervalMs)))
                .onSuccess(v -> v.getDisconnectFuture().onDone(disconnectF -> logger.warn("{} lost connection, will reconnect after {}ms", this, reConnectIntervalMs)))
                .onSuccess(v -> logger.info("{} connect success!", this))
                .onFailure(e -> logger.warn("{} connect failure, will reconnect after {}ms", this, reConnectIntervalMs, e))
                .future();

        // 如果是第一次连接，而且是同步连接，则进行阻塞，等待连接完成
        // 主要是给测试用
        if (!isReconnect && isSyncConnect) {
            connF.awaitUninterruptible();
        }

    }

    /**
     * 获取设备
     *
     * @return 设备
     */
    public Thing getThing() {
        return thing;
    }

    /**
     * 六足机器人联网
     *
     * @param isSyncConnect       是否同步连接
     * @param reConnectIntervalMs 重连间隔
     */
    public void connect(boolean isSyncConnect, long reConnectIntervalMs) {
        _connect(thing, isSyncConnect, false, reConnectIntervalMs);
    }

    /**
     * 导出六足机器人物模型到指定文件
     *
     * @param target 指定文件
     * @throws IOException 导出失败
     */
    public void dumpTsl(File target) throws IOException {
        thing.getUniqueThingCom(ThingRuntime.class).getThingSpec()
                .dump(new DumpTo.ToZip())
                .toZip(target);
    }

    /**
     * 输出六足机器人物模型
     *
     * @param output 指定输出
     */
    public void printTsl(PrintStream output) {
        thing.getUniqueThingCom(ThingRuntime.class).getThingSpec()
                .dump(new DumpTo.ToMap())
                .toMap()
                .forEach((componentId, tsl) -> output.printf("%s: %s%n", componentId, tsl));
    }

    /**
     * 销毁六足机器人
     *
     * @return Future
     */
    public ListenableFuture<Void> destroy() {
        return thing.destroy()
                .onDone(f -> logger.info("{} is destroyed!", this))
                .awaitUninterruptible();
    }

    @Override
    public String toString() {
        return _string;
    }

}
