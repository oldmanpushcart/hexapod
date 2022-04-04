package com.github.oldmanpushcart.hexapod.platform.gait.fn;

import com.github.oldmanpushcart.hexapod.platform.gait.Limb;
import com.github.oldmanpushcart.hexapod.platform.gait.Selector;
import io.github.oldmanpushcart.hexapod.api.Joint;

import java.util.HashMap;
import java.util.function.Supplier;

import static com.github.oldmanpushcart.hexapod.platform.gait.fn.JointFn.Filter.*;
import static com.github.oldmanpushcart.hexapod.platform.util.HexMath.fraction;
import static java.lang.Math.PI;

/**
 * 肢体函数
 */
public class LimbFn {

    private LimbFn() {

    }

    /**
     * 肢体映射
     */
    public interface Mapping extends Supplier<java.util.Map<Joint, Double>> {

        /**
         * 抬起肢体
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping up(Limb... limbs) {
            return () -> new HashMap<>() {{
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isKnee)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 8, 20)));
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isAnk)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 5, 20)));
            }};
        }

        /**
         * 放下肢体
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping down(Limb... limbs) {
            return () -> new HashMap<>() {{
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isKnee)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 10, 20)));
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isAnk)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 5, 20)));
            }};
        }

        /**
         * 顺时针移动肢体
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping forward(Limb... limbs) {
            return () -> new HashMap<>() {{
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isHip)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 8, 20)));
            }};
        }

        /**
         * 逆时针移动肢体
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping backward(Limb... limbs) {
            return () -> new HashMap<>() {{
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isHip)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 12, 20)));
            }};
        }

        /**
         * 归位肢体
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping homing(Limb... limbs) {
            return () -> new HashMap<>() {{
                Selector.select()
                        .filter(inLimbs(limbs))
                        .filter(isHip)
                        .selected()
                        .forEach(joint -> put(joint, fraction(PI, 10, 20)));
            }};
        }

        /**
         * 重置所有关节状态
         *
         * @param limbs 指定肢体
         * @return 肢体函数
         */
        static Mapping reset(Limb... limbs) {
            return () -> new HashMap<>() {{
                putAll(homing(limbs).get());
                putAll(down(limbs).get());
            }};
        }

    }
}
