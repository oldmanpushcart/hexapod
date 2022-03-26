package io.github.oldmanpushcart.hexapod.thing.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class HexapodTestCase extends ThingSupport {

    @Test
    public void test$hexapod$printTsl() {
        hexapod.printTsl(System.out);
    }

    @Test
    public void test$hexapod$dumpTsl() throws IOException {
        hexapod.dumpTsl(new File("hexapod-tsl.zip"));
    }

}
