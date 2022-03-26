package io.github.oldmanpushcart.hexapod.thing.test;

import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.api.Posture;
import org.junit.Test;

public class HexPostureCompTestCase extends ThingSupport {

    @Test
    public void test() throws Exception {

        while (true) {
            hexPostureComp.change(new Posture[]{
                    new Posture(500).map(Joint.values(), Math.PI / 2),
                    new Posture(500)
                            .map(Joint.select().is(Joint.right).not(Joint.hip).selected(), Math.PI / 6 * 5)
                            .map(Joint.select().is(Joint.left).not(Joint.hip).selected(), Math.PI - Math.PI / 6 * 5)
            }).syncUninterruptible();
        }

    }


}
