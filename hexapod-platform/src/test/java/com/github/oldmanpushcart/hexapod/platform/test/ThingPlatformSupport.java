package com.github.oldmanpushcart.hexapod.platform.test;

import com.github.oldmanpushcart.hexapod.platform.test.message.QaThingMessageGroupListener;
import com.github.oldmanpushcart.hexapod.platform.test.message.QaThingPostMessageListener;
import com.github.oldmanpushcart.hexapod.platform.test.message.QaThingReplyMessageListener;
import io.github.athingx.athing.aliyun.platform.ThingMessageConsumerBuilder;
import io.github.athingx.athing.aliyun.platform.ThingPlatformAccess;
import io.github.athingx.athing.aliyun.platform.ThingPlatformBuilder;
import io.github.athingx.athing.standard.platform.ThingPlatform;
import io.github.athingx.athing.standard.platform.ThingPlatformException;
import io.github.athingx.athing.standard.platform.message.ThingMessageListener;
import io.github.athingx.athing.standard.platform.message.ThingPostMessage;
import io.github.athingx.athing.standard.platform.message.ThingReplyMessage;
import io.github.athingx.athing.thing.monitor.ThingMonitorCom;
import io.github.athingx.athing.thing.monitor.info.ThingInfoCom;
import io.github.athingx.athing.thing.monitor.usage.ThingUsageCom;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.String.format;

public class ThingPlatformSupport {

    // 基础常量
    protected static final Properties properties = loadingProperties(new Properties());
    protected static final String PRODUCT_ID = $("athing.product.id");
    protected static final String THING_ID = $("athing.thing.id");

    protected static ThingPlatform platform;
    protected static final QaThingReplyMessageListener qaThingReplyMessageListener = new QaThingReplyMessageListener();
    protected static final QaThingPostMessageListener qaThingPostMessageListener = new QaThingPostMessageListener();

    private static final ThingPlatformAccess THING_PLATFORM_ACCESS = new ThingPlatformAccess(
            $("athing-platform.access.id"),
            $("athing-platform.access.secret")
    );

    private static ThingPlatform initPuppetThingPlatform() throws ThingPlatformException {
        return new ThingPlatformBuilder("cn-shanghai", THING_PLATFORM_ACCESS)
                .product(PRODUCT_ID,
                        PostureThingCom.class,
                        ThingUsageCom.class,
                        ThingInfoCom.class
                )
                .consumer(new ThingMessageConsumerBuilder()
                        .access(THING_PLATFORM_ACCESS)
                        .connection($("athing-platform.jms.connection-url"))
                        .group($("athing-platform.jms.group"))
                        .listener(new QaThingMessageGroupListener(new ThingMessageListener[]{
                                qaThingReplyMessageListener,
                                qaThingPostMessageListener
                        }))
                )
                .build();
    }

    public <T extends ThingReplyMessage> T waitingForReplyMessageByToken(String token) throws InterruptedException {
        return qaThingReplyMessageListener.waitingForReplyMessageByToken(token);
    }

    public <T extends ThingPostMessage> T waitingForPostMessageByToken(String token) throws InterruptedException {
        return qaThingPostMessageListener.waitingForPostMessageByToken(token);
    }

    @BeforeClass
    public static void initialization() throws Exception {
        platform = initPuppetThingPlatform();
    }

    private static String $(String name) {
        return properties.getProperty(name);
    }

    /**
     * 初始化配置文件
     *
     * @param properties 配置信息
     * @return 配置信息
     */
    private static Properties loadingProperties(Properties properties) {

        // 读取配置文件
        final File file = new File(System.getProperties().getProperty("athing-qatest.properties.file"));

        // 检查文件是否存在
        if (!file.exists()) {
            throw new RuntimeException(format("properties file: %s not existed!", file.getAbsolutePath()));
        }

        // 检查文件是否可读
        if (!file.canRead()) {
            throw new RuntimeException(format("properties file: %s can not read!", file.getAbsolutePath()));
        }

        // 加载配置文件
        try (final InputStream is = new FileInputStream(file)) {
            properties.load(is);
            return properties;
        } catch (Exception cause) {
            throw new RuntimeException(format("properties file: %s load error!", file.getAbsoluteFile()), cause);
        }

    }

}
