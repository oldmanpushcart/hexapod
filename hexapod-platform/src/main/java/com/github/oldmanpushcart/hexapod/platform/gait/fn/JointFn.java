package com.github.oldmanpushcart.hexapod.platform.gait.fn;

import com.github.oldmanpushcart.hexapod.platform.gait.Limb;
import io.github.oldmanpushcart.hexapod.api.Joint;

import java.util.Set;
import java.util.function.Predicate;

import static io.github.oldmanpushcart.hexapod.api.Joint.*;

public class JointFn {

    private JointFn() {

    }


    /**
     * 关节过滤器
     */
    public interface Filter extends Predicate<Joint> {

        static Filter inJoints(Joint... joints) {
            return joint -> Set.of(joints).contains(joint);
        }

        static Filter inLimbs(Limb... limbs) {
            return joint -> {
                for (final Limb limb : limbs) {
                    final boolean match;
                    switch (limb) {
                        case L_F -> match = isLeft.and(isFront).test(joint);
                        case L_M -> match = isLeft.and(isMiddle).test(joint);
                        case L_H -> match = isLeft.and(isHind).test(joint);
                        case R_F -> match = isRight.and(isFront).test(joint);
                        case R_M -> match = isRight.and(isMiddle).test(joint);
                        case R_H -> match = isRight.and(isHind).test(joint);
                        default -> match = false;
                    }
                    if (match) {
                        return true;
                    }
                }
                return false;
            };
        }

        Filter isLeft = inJoints(
                L_F_H, L_F_K, L_F_A,
                L_M_H, L_M_K, L_M_A,
                L_H_H, L_H_K, L_H_A
        );

        Filter isRight = inJoints(
                R_F_H, R_F_K, R_F_A,
                R_M_H, R_M_K, R_M_A,
                R_H_H, R_H_K, R_H_A
        );

        Filter isFront = inJoints(
                L_F_H, L_F_K, L_F_A,
                R_F_H, R_F_K, R_F_A
        );

        Filter isMiddle = inJoints(
                L_M_H, L_M_K, L_M_A,
                R_M_H, R_M_K, R_M_A
        );

        Filter isHind = inJoints(
                L_H_H, L_H_K, L_H_A,
                R_H_H, R_H_K, R_H_A
        );


        Filter isAnk = inJoints(
                L_F_A, L_M_A, L_H_A,
                R_F_A, R_M_A, R_H_A
        );

        Filter isKnee = inJoints(
                L_F_K, L_M_K, L_H_K,
                R_F_K, R_M_K, R_H_K
        );

        Filter isHip = inJoints(
                L_F_H, L_M_H, L_H_H,
                R_F_H, R_M_H, R_H_H
        );

    }
}
