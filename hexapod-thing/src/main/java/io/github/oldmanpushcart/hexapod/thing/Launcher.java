package io.github.oldmanpushcart.hexapod.thing;

import com.google.gson.GsonBuilder;
import io.github.athingx.athing.aliyun.thing.ThingOption;
import io.github.athingx.athing.aliyun.thing.runtime.access.ThingAccess;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;
import static io.github.oldmanpushcart.hexapod.thing.util.CheckUtils.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Launcher {

    private Launcher(String[] args) throws Exception {
        final OptionSet options = parseOptions(args);
        final ThingAccess access = optionThingAccess(options);
        final String remote = "ssl://%s.iot-as-mqtt.cn-shanghai.aliyuncs.com:443".formatted(access.getProductId());

        final Hexapod hexapod = new Hexapod(new URI(remote), access, new ThingOption());
        try {
            // 导出物模型
            if (options.has("dump")) {
                if (options.hasArgument("dump")) {
                    final File file = new File((String) options.valueOf("dump"));
                    hexapod.dumpTsl(file);
                    System.out.printf("dump completed: %s%n", file.getPath());
                } else {
                    hexapod.printTsl(System.out);
                    System.out.println("dump completed");
                }
            }

            // 机器人执行
            else {
                hexapod.connect(false, 1000L * 30);
                System.out.printf("%s started%n", hexapod);
                synchronized (this) {
                    wait();
                }
            }
        } finally {
            hexapod.destroy();
        }

    }

    private OptionSet parseOptions(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("access").withRequiredArg().ofType(String.class).required();
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

    public static void main(String... args) {
        try {
            new Launcher(args);
        } catch (Exception cause) {
            System.err.printf("hexapod start failed, because: %s%n", cause.getLocalizedMessage());
            cause.printStackTrace(System.err);
            System.exit(-1);
        }
    }

}
