package io.github.oldmanpushcart.hexapod.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 关节序列
 */
public enum Joint {

    L_F_H, L_F_K, L_F_A,
    L_M_H, L_M_K, L_M_A,
    L_H_H, L_H_K, L_H_A,
    R_F_H, R_F_K, R_F_A,
    R_M_H, R_M_K, R_M_A,
    R_H_H, R_H_K, R_H_A;

    public boolean is(Predicate<Joint> predicate) {
        return predicate.test(this);
    }

    public static Selector select() {
        return new Selector() {

            private final List<Predicate<Joint>> predicates = new ArrayList<>();

            @Override
            public Selector is(Predicate<Joint> predicate) {
                predicates.add(predicate);
                return this;
            }

            @Override
            public Selector not(Predicate<Joint> predicate) {
                predicates.add(joint -> !predicate.test(joint));
                return this;
            }

            @Override
            public Joint[] selected() {
                return Stream.of(Joint.values())
                        .filter(joint -> predicates.stream().allMatch(target -> target.test(joint)))
                        .toList()
                        .toArray(new Joint[0]);
            }

        };
    }

    public interface Selector {

        Selector is(Predicate<Joint> fn);

        Selector not(Predicate<Joint> fn);

        Joint[] selected();

    }

    private static final class JointPredicate implements Predicate<Joint> {

        private final List<Joint> list;

        public JointPredicate(Joint... joints) {
            this.list = List.of(joints);
        }

        @Override
        public boolean test(Joint joint) {
            return list.contains(joint);
        }

    }

    public static final Predicate<Joint> any = joint -> true;

    public static final Predicate<Joint> left = new JointPredicate(
            L_F_H, L_F_K, L_F_A,
            L_M_H, L_M_K, L_M_A,
            L_H_H, L_H_K, L_H_A
    );

    public static final Predicate<Joint> right = new JointPredicate(
            R_F_H, R_F_K, R_F_A,
            R_M_H, R_M_K, R_M_A,
            R_H_H, R_H_K, R_H_A
    );

    public static final Predicate<Joint> front = new JointPredicate(
            L_F_H, L_F_K, L_F_A,
            R_F_H, R_F_K, R_F_A
    );

    public static final Predicate<Joint> middle = new JointPredicate(
            L_M_H, L_M_K, L_M_A,
            R_M_H, R_M_K, R_M_A
    );

    public static final Predicate<Joint> hind = new JointPredicate(
            L_H_H, L_H_K, L_H_A,
            R_H_H, R_H_K, R_H_A
    );


    public static final Predicate<Joint> ank = new JointPredicate(
            L_F_A, L_M_A, L_H_A,
            R_F_A, R_M_A, R_H_A
    );

    public static final Predicate<Joint> knee = new JointPredicate(
            L_F_K, L_M_K, L_H_K,
            R_F_K, R_M_K, R_H_K
    );

    public static final Predicate<Joint> hip = new JointPredicate(
            L_F_H, L_M_H, L_H_H,
            R_F_H, R_M_H, R_H_H
    );

}
