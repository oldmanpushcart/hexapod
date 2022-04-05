package io.github.oldmanpushcart.hexapod.api;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * 姿势
 */
public class Posture {

    private final long duration;

    private Double lfh;
    private Double lfk;
    private Double lfa;
    private Double lmh;
    private Double lmk;
    private Double lma;
    private Double lhh;
    private Double lhk;
    private Double lha;
    private Double rfh;
    private Double rfk;
    private Double rfa;
    private Double rmh;
    private Double rmk;
    private Double rma;
    private Double rhh;
    private Double rhk;
    private Double rha;

    /**
     * 构建姿势
     *
     * @param duration 姿势时长
     */
    public Posture(long duration) {
        this.duration = duration;
    }

    /**
     * 获取姿势时长
     *
     * @return 姿势时长
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 遍历关节弧度
     *
     * @param consumer 遍历函数
     */
    public void forEach(BiConsumer<Joint, Double> consumer) {
        Stream.of(Joint.values())
                .forEach(joint -> {
                    final Double radian = getRadian(joint);
                    if (null != radian) {
                        consumer.accept(joint, radian);
                    }
                });
    }

    /**
     * 获取关节弧度
     *
     * @param joint 关节
     * @return 弧度
     */
    public Double getRadian(Joint joint) {
        return switch (joint) {
            case L_F_H -> lfh;
            case L_F_K -> lfk;
            case L_F_A -> lfa;
            case L_M_H -> lmh;
            case L_M_K -> lmk;
            case L_M_A -> lma;
            case L_H_H -> lhh;
            case L_H_K -> lhk;
            case L_H_A -> lha;
            case R_F_H -> rfh;
            case R_F_K -> rfk;
            case R_F_A -> rfa;
            case R_M_H -> rmh;
            case R_M_K -> rmk;
            case R_M_A -> rma;
            case R_H_H -> rhh;
            case R_H_K -> rhk;
            case R_H_A -> rha;
        };
    }

    /**
     * 设置关节弧度
     *
     * @param joint  关节
     * @param radian 弧度
     */
    public void put(Joint joint, Double radian) {
        switch (joint) {
            case L_F_H -> lfh = radian;
            case L_F_K -> lfk = radian;
            case L_F_A -> lfa = radian;
            case L_M_H -> lmh = radian;
            case L_M_K -> lmk = radian;
            case L_M_A -> lma = radian;
            case L_H_H -> lhh = radian;
            case L_H_K -> lhk = radian;
            case L_H_A -> lha = radian;
            case R_F_H -> rfh = radian;
            case R_F_K -> rfk = radian;
            case R_F_A -> rfa = radian;
            case R_M_H -> rmh = radian;
            case R_M_K -> rmk = radian;
            case R_M_A -> rma = radian;
            case R_H_H -> rhh = radian;
            case R_H_K -> rhk = radian;
            case R_H_A -> rha = radian;
        }
    }

    /**
     * 设置关节组弧度
     *
     * @param joints 关节组
     * @param radian 弧度
     */
    public void put(Joint[] joints, Double radian) {
        for (final Joint joint : joints) {
            put(joint, radian);
        }
    }

}
