package io.github.oldmanpushcart.hexapod.thing;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.google.gson.GsonBuilder;
import io.github.athingx.athing.aliyun.thing.ThingOption;
import io.github.athingx.athing.aliyun.thing.runtime.access.ThingAccess;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;
import static io.github.oldmanpushcart.hexapod.thing.util.CheckUtils.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 启动器
 */
public class Launcher {

    private Launcher(String[] args) throws Exception {

        // 解析日志
        final OptionSet options = parseOptions(args);

        // 加载阿里云设备访问账密
        final ThingAccess access = optionThingAccess(options);
        info("PRODUCT-ID", access.getProductId());
        info("THING-ID", access.getThingId());

        // 启动日志
        optionLogbackCfg(options);
        info("LOGGER", "SUCCESS");

        // 六足机器人初始化
        final String remote = "ssl://%s.iot-as-mqtt.cn-shanghai.aliyuncs.com:443".formatted(access.getProductId());
        final Hexapod hexapod = new Hexapod(new URI(remote), access, new ThingOption());
        try {

            // 导出物模型
            if (options.has("dump")) {
                if (options.hasArgument("dump")) {
                    final File file = new File((String) options.valueOf("dump"));
                    info("DUMP-TO", file.getPath());
                    hexapod.dumpTsl(file);
                } else {
                    info("DUMP-TO", "CONSOLE");
                    hexapod.printTsl(System.out);
                }
            }

            // 机器人执行
            else {
                hexapod.connect(false, 1000L * 30);
                info("BOOTSTRAP", "SUCCESS");
                bootstrapLatch.await();
            }

        } finally {
            hexapod.destroy()
                    .onDone(f -> shutdownLatch.countDown())
                    .awaitUninterruptible();
        }

    }

    private OptionSet parseOptions(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("access").withRequiredArg().ofType(String.class).required();
        parser.accepts("logback").withOptionalArg().ofType(String.class).required();
        parser.accepts("dump").withOptionalArg().ofType(String.class);
        return parser.parse(args);
    }

    private ThingAccess optionThingAccess(OptionSet options) {
        final File file = new File((String) options.valueOf("access"));
        checkArgument(file.exists(), "not exists: %s".formatted(file.getPath()));
        checkArgument(file.canRead(), "not readable: %s".formatted(file.getPath()));
        try (final FileReader reader = new FileReader(file)) {
            final ThingAccess access = new GsonBuilder()
                    .setFieldNamingPolicy(LOWER_CASE_WITH_DASHES)
                    .create()
                    .fromJson(reader, ThingAccess.class);
            checkArgument(isNotBlank(access.getProductId()), "product-id is missing");
            checkArgument(isNotBlank(access.getThingId()), "thing-id is missing");
            checkArgument(isNotBlank(access.getSecret()), "secret is missing");
            return access;
        } catch (Exception cause) {
            throw new IllegalArgumentException("load access error: %s, from %s".formatted(
                    cause.getLocalizedMessage(),
                    file.getPath()
            ));
        }
    }

    private void optionLogbackCfg(OptionSet options) throws Exception {
        final File file = new File((String) options.valueOf("logback"));
        checkArgument(file.exists(), "not exists: %s".formatted(file.getPath()));
        checkArgument(file.canRead(), "not readable: %s".formatted(file.getPath()));
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        configurator.doConfigure(file);
    }


    private static final CountDownLatch bootstrapLatch = new CountDownLatch(1);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    // 控制台输出: info
    private static void info(String name, String message) {
        output(System.out, name, message);
    }

    // 控制台输出: error
    private static void err(String name, String message) {
        output(System.err, name, message);
    }

    // 控制台输出
    private static void output(PrintStream ps, String name, String message) {
        ps.printf("%10s : %s%n", name, message);
        ps.flush();
    }

    public static void main(String... args) {

        // 添加CTRL_C回调，处理关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bootstrapLatch.countDown();
            System.out.printf("%n");
            System.out.flush();
            try {
                if (shutdownLatch.await(60, TimeUnit.SECONDS)) {
                    info("SHUTDOWN", "SUCCESS");
                } else {
                    err("SHUTDOWN", "TIMEOUT; 60sec");
                }
            } catch (InterruptedException e) {
                // ignore...
            }
        }, "hexapod-shutdown"));

        // 启动六足
        try {
            new Launcher(args);
        } catch (Exception cause) {
            err("BOOTSTRAP", "FAILURE; %s".formatted(cause.getLocalizedMessage()));
            cause.printStackTrace(System.err);
            System.exit(-1);
        }

    }

}
