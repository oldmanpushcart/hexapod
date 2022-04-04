package io.github.oldmanpushcart.hexapod.thing.test;

import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.thing.manager.impl.ServoCodec;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class HackTests {

    @Test
    public void hack() {

        final int[] pws = new int[]{

                1500, 1000, 500, 1500, 1000, 500, 1500, 1000, 500, 1500, 2000, 2500, 1500, 2000, 2500, 1500, 2000, 2500
                // 1500, 1500, 1000, 1500, 1500, 1000, 1500, 1500, 1000, 1500, 1500, 2000, 1500, 1500, 2000, 1500, 1500, 2000

                // 1700, 1500, 1000, 1300, 1300, 1000, 1700, 1500, 1000, 1700, 1700, 2000, 1300, 1500, 2000, 1700, 1700, 2000,
                // 1700, 1500, 1000, 1300, 1500, 1000, 1700, 1500, 1000, 1700, 1500, 2000, 1300, 1500, 2000, 1700, 1500, 2000,
                // 1300, 1300, 1000, 1700, 1500, 1000, 1300, 1300, 1000, 1300, 1500, 2000, 1700, 1700, 2000, 1300, 1500, 2000,
                // 1300, 1500, 1000, 1700, 1500, 1000, 1300, 1500, 1000, 1300, 1500, 2000, 1700, 1500, 2000, 1300, 1500, 2000
        };

        Assert.assertEquals(18, pws.length);

        for (final Joint joint : Joint.values()) {
            final double radian = ServoCodec.pwToRadian(pws[mapping.get(joint) - 1]);
            final boolean isLeft = joint.name().startsWith("R_");
            final double degrees = Math.toDegrees(radian);

            System.out.printf(".map(%s, fraction(PI, %2d, 20)) // %3.2f%n", joint.name(), (int) degrees / 9, isLeft ? 180 - degrees : degrees);
        }

    }


    /**
     * 步态控制转换为舵机控制命令
     * {@code
     * <p>
     * +-----+-----+----+
     * | L_H | HIP | 01 |
     * | L_H | KNE | 02 |
     * | L_H | ANK | 03 |
     * +-----+-----+----+
     * | L_M | HIP | 04 |
     * | L_M | KNE | 05 |
     * | L_M | ANK | 06 |
     * +-----+-----+----+
     * | L_F | HIP | 07 |
     * | L_F | KNE | 08 |
     * | L_F | ANK | 09 |
     * +-----+-----+----+
     * | R_F | HIP | 16 |
     * | R_F | KNE | 17 |
     * | R_F | ANK | 18 |
     * +-----+-----+----+
     * | R_M | HIP | 13 |
     * | R_M | KNE | 14 |
     * | R_M | ANK | 15 |
     * +-----+-----+----+
     * | R_H | HIP | 10 |
     * | R_H | KNE | 11 |
     * | R_H | ANK | 12 |
     * +-----+-----+----+
     * <p>
     * }
     */
    private static final Map<Joint, Integer> mapping = Collections.unmodifiableMap(new LinkedHashMap<>() {{

        put(Joint.L_H_H, 0x01);
        put(Joint.L_H_K, 0x02);
        put(Joint.L_H_A, 0x03);

        put(Joint.L_M_H, 0x04);
        put(Joint.L_M_K, 0x05);
        put(Joint.L_M_A, 0x06);

        put(Joint.L_F_H, 0x07);
        put(Joint.L_F_K, 0x08);
        put(Joint.L_F_A, 0x09);

        put(Joint.R_H_H, 0x10);
        put(Joint.R_H_K, 0x11);
        put(Joint.R_H_A, 0x12);

        put(Joint.R_M_H, 0x0d);
        put(Joint.R_M_K, 0x0e);
        put(Joint.R_M_A, 0x0f);

        put(Joint.R_F_H, 0x0a);
        put(Joint.R_F_K, 0x0b);
        put(Joint.R_F_A, 0x0c);

    }});

}
