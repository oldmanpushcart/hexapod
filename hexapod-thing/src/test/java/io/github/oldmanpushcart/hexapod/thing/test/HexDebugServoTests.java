package io.github.oldmanpushcart.hexapod.thing.test;

import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.thing.manager.PostureMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.impl.ServoCodec;
import org.junit.Assert;
import org.junit.Test;

import static io.github.oldmanpushcart.hexapod.api.Joint.*;

public class HexDebugServoTests {

    @Test
    public void test$debug$servo() {

        final int[] pws = new int[]{
                // 1500, 1500, 1000, 1500, 1500, 1000, 1500, 1500, 1000, 1500, 1500, 2000, 1500, 1500, 2000, 1500, 1500, 2000,

                // 1900,1500,1000,1100,1300,1000,1900,1500,1000,1900,1700,2000,1100,1500,2000,1900,1700,2000,
                // 1900,1500,1000,1100,1500,1000,1900,1500,1000,1900,1500,2000,1100,1500,2000,1900,1500,2000,
                // 1100,1300,1000,1900,1500,1000,1100,1300,1000,1100,1500,2000,1900,1700,2000,1100,1500,2000,
                1100,1500,1000,1900,1500,1000,1100,1500,1000,1100,1500,2000,1900,1500,2000,1100,1500,2000,
        };

        Assert.assertEquals(18, pws.length);

        for (final Joint joint : new Joint[]{
                L_F_H, L_F_K, L_F_A,
                R_M_H, R_M_K, R_M_A,
                L_H_H, L_H_K, L_H_A,

                R_F_H, R_F_K, R_F_A,
                L_M_H, L_M_K, L_M_A,
                R_H_H, R_H_K, R_H_A,

        }) {
            final double radian = ServoCodec.pwToRadian(pws[PostureMgr.mapping.get(joint) - 1]);
            final boolean isLeft = joint.name().startsWith("R_");
            final double degrees = Math.toDegrees(radian);

            System.out.printf(".map(%s, fraction(PI, %2d, 20)) // %3.2f%n",
                    joint.name(), (int) degrees / 9, isLeft ? 180 - degrees : degrees);

            if(joint == L_H_A) {
                System.out.println();
            }
        }

    }

}
