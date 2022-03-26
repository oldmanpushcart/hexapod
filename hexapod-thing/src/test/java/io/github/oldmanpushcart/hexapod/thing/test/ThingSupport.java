package io.github.oldmanpushcart.hexapod.thing.test;

import io.github.athingx.athing.aliyun.thing.ThingOption;
import io.github.athingx.athing.aliyun.thing.runtime.access.ThingAccess;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;
import io.github.oldmanpushcart.hexapod.thing.Hexapod;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import static java.lang.String.format;

public class ThingSupport {

    // 基础常量
    protected static final Properties properties = loadingProperties(new Properties());
    protected static final String PRODUCT_ID = $("athing.product.id");
    protected static final String THING_ID = $("athing.thing.id");

    private static final ThingAccess THING_ACCESS = new ThingAccess(
            $("athing.product.id"),
            $("athing.thing.id"),
            $("athing.thing.secret")
    );

    protected static Hexapod hexapod;
    protected static PostureThingCom hexPostureComp;

    @BeforeClass
    public static void initialization() throws Exception {
        hexapod = new Hexapod(new URI($("athing.thing.server-url")), THING_ACCESS, new ThingOption());
        hexapod.connect(true, 1000L * 10);
        hexPostureComp = hexapod.getThing().getUniqueThingCom(PostureThingCom.class);
    }

    @AfterClass
    public static void destroy() {
        hexapod.destroy().awaitUninterruptible();
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

    private static String $(String name) {
        return properties.getProperty(name);
    }

}
