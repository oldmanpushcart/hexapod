package io.github.oldmanpushcart.hexapod.api.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.PI;
import static java.math.BigDecimal.valueOf;

/**
 * 脉宽弧度计算
 * RaPwNumber
 */
public class RaPwMath {

    /**
     * 脉宽最小值
     */
    public static final int PW_MIN = 500;

    /**
     * 脉宽最大值
     */
    public static final int PW_MAX = 2500;

    /**
     * 弧度最小值
     */
    public static final BigDecimal RA_MIN = valueOf(0);

    /**
     * 弧度最大值
     */
    public static final BigDecimal RA_MAX = valueOf(PI);

    /**
     * 1/4弧度
     */
    public static final BigDecimal RA_QUARTER = RA_MAX.divide(valueOf(4), RoundingMode.HALF_UP);

    /**
     * 1/3弧度
     */
    public static final BigDecimal RA_ONE_THIRD = RA_MAX.divide(valueOf(3), RoundingMode.HALF_UP);

    /**
     * 1/2弧度
     */
    public static final BigDecimal RA_HALF = RA_MAX.divide(valueOf(2), RoundingMode.HALF_UP);

    /**
     * 弧脉比
     */
    public static final BigDecimal RA_PW_RATE = valueOf(PW_MAX - PW_MIN).divide(RA_MAX.subtract(RA_MIN), RoundingMode.HALF_EVEN);

    private static int _pw(int pw) {
        return Math.min(Math.max(pw, PW_MIN), PW_MAX);
    }

    private static BigDecimal _ra(double radian) {
        BigDecimal ra = valueOf(radian);
        while (RA_MAX.compareTo(ra) < 0) {
            ra = ra.subtract(RA_MAX);
        }
        return ra;
    }

    /**
     * 脉宽转弧度
     *
     * @param pw 脉宽
     * @return 弧度
     */
    public static BigDecimal pwToRa(int pw) {
        return valueOf(_pw(pw) - PW_MIN).divide(RA_PW_RATE, RoundingMode.HALF_UP);
    }

    /**
     * 弧度转脉宽
     *
     * @param radian 弧度
     * @return 脉宽
     */
    public static int raToPw(double radian) {
        if (radian < 0) {
            throw new IllegalArgumentException("require not negative!");
        }
        return PW_MIN + RA_PW_RATE.multiply(_ra(radian)).intValue();
    }

}
