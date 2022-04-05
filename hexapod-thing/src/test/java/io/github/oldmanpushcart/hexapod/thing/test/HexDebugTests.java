package io.github.oldmanpushcart.hexapod.thing.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class HexDebugTests extends ThingSupport {

    @Test
    public void test$debug$tsl$print() {
        hexapod.printTsl(System.out);
    }

    @Test
    public void test$debug$tsl$dump() throws IOException {
        hexapod.dumpTsl(new File("hexapod-tsl.zip"));
    }

}
