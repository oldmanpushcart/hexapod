package io.github.oldmanpushcart.hexapod.thing.manager;

import java.io.IOException;

/**
 * 舵机控制器
 */
public interface ServoMgr {

    /**
     * 控制舵机旋转
     *
     * @param duration  旋转时长
     * @param positions 舵机位置
     * @throws IOException 控制失败
     */
    void rotate(long duration, Position[] positions) throws IOException;

    /**
     * 舵机位置
     *
     * @param index 编号
     * @param pw    脉宽
     */
    record Position(int index, int pw) {

        @Override
        public String toString() {
            return "[%d]=%d".formatted(index, pw);
        }

    }

}
