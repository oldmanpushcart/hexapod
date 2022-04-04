package com.github.oldmanpushcart.hexapod.platform.util;

/**
 * 数学公式
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

}
