package io.github.oldmanpushcart.hexapod.thing.manager.impl;

import static java.lang.Math.PI;

/**
 * 舵机编解码常量
 */
public interface ServoCodec {

    /*
     * 协议头
     */
    byte[] MAGIC_CODE = {0x55, 0x55};

    /*
     * 控制指令
     */
    byte CMD_SERVO_MOVE = 0x03;

    /*
     * 最大舵机个数
     */
    int MAX_SERVO_NUM = 32;

    /*
     * MC区大小
     */
    int SIZE_OF_MC = MAGIC_CODE.length;

    /*
     * 指令区大小
     */
    int SIZE_OF_CMD = 1;

    /*
     * 单个舵机控制区大小
     */
    int SIZE_OF_SINGLE_SERVO_CMD = 3;

    /*
     * 数据长度区大小
     */
    int SIZE_OF_DATA_LENGTH = 1;

    /*
     * 舵机数量区大小
     */
    int SIZE_OF_NUM = 1;

    /*
     * 时间区大小
     */
    int SIZE_OF_DURATION = 2;

    /**
     * 计算数据长度
     *
     * @param num 舵机数量
     * @return 数据长度
     */
    static int computeFrameDataLength(int num) {
        return SIZE_OF_DATA_LENGTH
                + SIZE_OF_CMD
                + SIZE_OF_NUM
                + SIZE_OF_DURATION
                + SIZE_OF_SINGLE_SERVO_CMD * num;
    }

    /**
     * 计算帧长度
     *
     * @return 帧长度
     */
    static int computeFrameLength() {
        return SIZE_OF_MC
                + SIZE_OF_DATA_LENGTH
                + computeFrameDataLength(MAX_SERVO_NUM);
    }


    /**
     * 脉宽最小值
     */
    int PW_MIN = 500;

    /**
     * 脉宽最大值
     */
    int PW_MAX = 2500;

    /**
     * 弧度最小值
     */
    double RA_MIN = 0d;

    /**
     * 弧度最大值
     */
    double RA_MAX = PI;

    /**
     * 弧脉比
     */
    double RA_PW_RATE = ((double) (PW_MAX - PW_MIN)) / (RA_MAX - RA_MIN);

    private static int _pw(int pw) {
        return Math.min(Math.max(pw, PW_MIN), PW_MAX);
    }

    private static double _ra(double radian) {
        return radian % RA_MAX;
    }

    /**
     * 脉宽转弧度
     *
     * @param pw 脉宽
     * @return 弧度
     */
    static double pwToRadian(int pw) {
        return ((double)(_pw(pw)-PW_MIN)) / RA_PW_RATE;
    }

    /**
     * 弧度转脉宽
     *
     * @param radian 弧度
     * @return 脉宽
     */
    static int radianToPw(double radian) {
        if (radian < 0) {
            throw new IllegalArgumentException("require not negative!");
        }
        return PW_MIN + (int)(RA_PW_RATE * _ra(radian));
    }

}
