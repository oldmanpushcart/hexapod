package com.github.oldmanpushcart.hexapod.platform.gait;

import com.github.oldmanpushcart.hexapod.platform.gait.fn.LimbFn;
import io.github.oldmanpushcart.hexapod.api.Posture;

import java.util.*;
import java.util.stream.Stream;

import static com.github.oldmanpushcart.hexapod.platform.gait.fn.JointFn.Filter.isRight;
import static com.github.oldmanpushcart.hexapod.platform.gait.Limb.*;
import static com.github.oldmanpushcart.hexapod.platform.gait.fn.LimbFn.Mapping.*;
import static java.lang.Math.PI;

/**
 * 步态，描述了一个动作，由多个姿态构成
 */
public class Gait implements Iterable<Gait> {

    private final Gait parent;
    private final Posture posture;

    private Gait(Gait parent, Posture posture) {
        this.parent = parent;
        this.posture = posture;
    }

    /**
     * 步态
     *
     * @param parent   父步态
     * @param duration 步态时长
     */
    private Gait(Gait parent, long duration) {
        this.parent = parent;
        this.posture = new Posture(duration);
    }

    /**
     * 肢体动作
     *
     * @param fns 肢体函数
     * @return this
     */
    public Gait limb(LimbFn.Mapping... fns) {
        Stream.of(fns).forEach(fn -> fn.get().forEach(posture::put));
        return this;
    }

    /**
     * 创建步态
     *
     * @param duration 步态时长
     * @param fns      肢体映射
     * @return 步态
     */
    public static Gait create(long duration, LimbFn.Mapping... fns) {
        return new Gait(null, duration).limb(fns);
    }

    /**
     * 创建步态
     *
     * @param duration 步态时长
     * @return 步态
     */
    public static Gait create(long duration) {
        return create(duration, new LimbFn.Mapping[0]);
    }

    /**
     * 下一个步态
     *
     * @param duration 步态时长
     * @return 下一个步态
     */
    public Gait next(long duration, LimbFn.Mapping... fns) {
        return new Gait(this, duration).limb(fns);
    }

    /**
     * 下一个步态
     *
     * @return 下一个步态
     */
    public Gait next(LimbFn.Mapping... fns) {
        return next(posture.getDuration(), fns);
    }

    /**
     * 追加目标步态到下一个
     *
     * @param repeat 重复次数
     * @param gait   目标步态
     * @return 步态
     */
    public Gait next(int repeat, Gait gait) {
        Gait next = this;
        for (int index = 0; index < repeat; index++) {
            next = next(gait);
        }
        return next;
    }

    /**
     * 追加目标步态到下一个
     *
     * @param gait 目标步态
     * @return 步态
     */
    public Gait next(Gait gait) {

        // 按照正序构建postures
        final List<Posture> postures = new ArrayList<>();
        for (final Gait current : gait) {
            postures.add(current.posture);
        }
        Collections.reverse(postures);

        // 按照正序添加
        Gait next = this;
        for (final Posture posture : postures) {
            next = new Gait(next, posture);
        }
        return next;
    }

    @Override
    public Iterator<Gait> iterator() {
        return new Iterator<>() {

            Gait current = Gait.this;

            @Override
            public boolean hasNext() {
                return null != current.parent;
            }

            @Override
            public Gait next() {
                final Gait next = current;
                current = current.parent;
                return next;
            }

        };
    }

    /**
     * 步态转换为姿势组
     *
     * @return 姿势组
     */
    public Posture[] toPostures() {
        final List<Posture> postures = new LinkedList<>();
        Gait current = this;
        while (null != current) {

            final Posture posture = new Posture(current.posture.getDuration());
            current.posture.forEach((joint, radian) ->
                    posture.put(
                            joint,
                            isRight.test(joint) ? PI - radian : radian
                    ));

            postures.add(posture);
            current = current.parent;
        }
        Collections.reverse(postures);
        return postures.toArray(new Posture[0]);
    }


    /**
     * 步态：站立
     *
     * @param duration 步态时长
     * @return 步态
     */
    public static Gait stand(long duration) {
        final Limb[] S1 = {L_F, R_M, L_H};
        final Limb[] S2 = {R_F, L_M, R_H};
        return Gait.create(duration)
                .limb(up(S1), homing(S1))
                .next(down(S1))
                .next(up(S2), homing(S2))
                .next(down(S2));
    }

    /**
     * 步态：前进
     *
     * @param duration 步态时长
     * @param step     步数
     * @return 步态
     */
    public static Gait moveForward(long duration, int step) {
        final Limb[] S1 = {L_F, R_M, L_H};
        final Limb[] S2 = {R_F, L_M, R_H};
        Gait forward = Gait.create(duration);
        for (int index = 0; index < step; index++) {
            forward = forward
                    .next(down(S1), backward(S1), up(S2), forward(S2))
                    .next(down(S2))
                    .next(up(S1), forward(S1), backward(S2))
                    .next(down(S1));
        }
        return forward;
    }

    /**
     * 步态：后退
     *
     * @param duration 步态时长
     * @param step     步数
     * @return 步态
     */
    public static Gait moveBackward(long duration, int step) {
        final Limb[] S1 = {L_F, R_M, L_H};
        final Limb[] S2 = {R_F, L_M, R_H};
        Gait fallback = Gait.create(duration);
        for (int index = 0; index < step; index++) {
            fallback = fallback
                    .next(down(S2), forward(S2), up(S1), backward(S1))
                    .next(down(S1))
                    .next(up(S2), backward(S2), forward(S1))
                    .next(down(S2));
        }
        return fallback;
    }

    /**
     * 步态：右转
     *
     * @param duration 步态时长
     * @param step     步数
     * @return 步态
     */
    public static Gait turnRight(long duration, int step) {
        final Limb[] S1 = {L_F, R_M, L_H};
        final Limb[] S2 = {R_F, L_M, R_H};
        Gait right = Gait.create(duration);
        for (int index = 0; index < step; index++) {
            right = right
                    .next(down(S1), homing(S1), up(S2), forward(R_F, R_H), backward(L_M))
                    .next(down(S1), homing(S1), down(S2))
                    .next(up(S1), homing(S1), down(S2), homing(S2))
                    .next(down(S1), homing(S1), down(S2), homing(S2));
        }
        return right;
    }

    /**
     * 步态：左转
     *
     * @param duration 步态时长
     * @param step     步数
     * @return 步态
     */
    public static Gait turnLeft(long duration, int step) {
        final Limb[] S1 = {L_F, R_M, L_H};
        final Limb[] S2 = {R_F, L_M, R_H};
        Gait left = Gait.create(duration);
        for (int index = 0; index < step; index++) {
            left = left
                    .next(down(S2), homing(S2), up(S1), forward(L_F, L_H), backward(R_M))
                    .next(down(S2), homing(S2), down(S1))
                    .next(up(S2), homing(S2), down(S1), homing(S1))
                    .next(down(S2), homing(S2), down(S1), homing(S1));
        }
        return left;
    }

}
