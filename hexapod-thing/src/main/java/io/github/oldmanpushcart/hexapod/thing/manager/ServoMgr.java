package io.github.oldmanpushcart.hexapod.thing.manager;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

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
    void rotate(long duration, Set<Position> positions) throws IOException;

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

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Position other
                    && index == other.index;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(index);
        }
    }

}
