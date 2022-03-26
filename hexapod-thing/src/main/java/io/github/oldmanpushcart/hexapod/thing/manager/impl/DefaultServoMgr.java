package io.github.oldmanpushcart.hexapod.thing.manager.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.oldmanpushcart.hexapod.thing.manager.InfoMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.ServoMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

import static io.github.oldmanpushcart.hexapod.thing.manager.impl.ServoCodec.computeFrameDataLength;
import static io.github.oldmanpushcart.hexapod.thing.manager.impl.ServoCodec.computeFrameLength;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

@Singleton
public class DefaultServoMgr implements ServoMgr, ServoCodec {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final InfoMgr infoMgr;
    private final ByteBuffer buffer = ByteBuffer.allocate(computeFrameLength()).order(LITTLE_ENDIAN);
    private final ByteChannel channel;

    @Inject
    public DefaultServoMgr(ByteChannel channel, InfoMgr infoMgr) {
        this.channel = channel;
        this.infoMgr = infoMgr;
    }

    @Override
    public void rotate(long duration, Position[] positions) throws IOException {
        synchronized (buffer) {
            buffer.clear();
            encodeByteBuffer(buffer, duration, positions);
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("{}/servo/rotate positions: {}", infoMgr.getName(), Arrays.asList(positions));
        }
    }

    /**
     * 编码舵机旋转指令
     *
     * @param buffer   数据缓冲
     * @param duration 旋转时长
     * @param positions   舵机位置
     */
    private static void encodeByteBuffer(ByteBuffer buffer, long duration, Position[] positions) {
        buffer
                .put(MAGIC_CODE)
                .put((byte) computeFrameDataLength(positions.length))
                .put(CMD_SERVO_MOVE)
                .put((byte) positions.length)
                .putShort((short) duration);
        Arrays.stream(positions).forEach(position -> buffer.put((byte) position.index()).putShort((short) position.pw()));
    }

}
