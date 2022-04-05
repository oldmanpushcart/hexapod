package com.github.oldmanpushcart.hexapod.platform.util;

/**
 * 六足相关数学公式
 */
public interface HexMath {

    /**
     * 求分数
     *
     * @param number      目标数
     * @param numerator   分子
     * @param denominator 分母
     * @return 目标数的几分之几
     */
    static double fraction(double number, double numerator, double denominator) {
        return number * numerator / denominator;
    }

    /**
     * 派
     *
     * @return 派
     */
    static double PI() {
        return Math.PI;
    }

    /**
     * 几分之几派
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 几分之几派
     */
    static double PI(double numerator, double denominator) {
        return fraction(PI(), numerator, denominator);
    }

}
