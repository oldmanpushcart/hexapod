package io.github.oldmanpushcart.hexapod.thing.manager.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.oldmanpushcart.hexapod.thing.manager.PostureMgr;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.jpromisor.Promise;
import io.github.oldmanpushcart.jpromisor.Promisor;
import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.api.Posture;
import io.github.oldmanpushcart.hexapod.thing.manager.InfoMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.ServoMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.oldmanpushcart.hexapod.api.math.RaPwMath.raToPw;

@Singleton
public class DefaultPostureMgr implements PostureMgr, Runnable {

    /**
     * 步态控制转换为舵机控制命令
     * {@code
     * <p>
     * +-----+-----+----+
     * | L_H | HIP | 01 |
     * | L_H | KNE | 02 |
     * | L_H | ANK | 03 |
     * +-----+-----+----+
     * | L_M | HIP | 04 |
     * | L_M | KNE | 05 |
     * | L_M | ANK | 06 |
     * +-----+-----+----+
     * | L_F | HIP | 07 |
     * | L_F | KNE | 08 |
     * | L_F | ANK | 09 |
     * +-----+-----+----+
     * | R_F | HIP | 16 |
     * | R_F | KNE | 17 |
     * | R_F | ANK | 18 |
     * +-----+-----+----+
     * | R_M | HIP | 13 |
     * | R_M | KNE | 14 |
     * | R_M | ANK | 15 |
     * +-----+-----+----+
     * | R_H | HIP | 10 |
     * | R_H | KNE | 11 |
     * | R_H | ANK | 12 |
     * +-----+-----+----+
     * <p>
     * }
     */
    private static final Map<Joint, Integer> mapping = Collections.unmodifiableMap(new LinkedHashMap<>() {{

        put(Joint.L_H_H, 0x01);
        put(Joint.L_H_K, 0x02);
        put(Joint.L_H_A, 0x03);

        put(Joint.L_M_H, 0x04);
        put(Joint.L_M_K, 0x05);
        put(Joint.L_M_A, 0x06);

        put(Joint.L_F_H, 0x07);
        put(Joint.L_F_K, 0x08);
        put(Joint.L_F_A, 0x09);

        put(Joint.R_H_H, 0x10);
        put(Joint.R_H_K, 0x11);
        put(Joint.R_H_A, 0x12);

        put(Joint.R_M_H, 0x0d);
        put(Joint.R_M_K, 0x0e);
        put(Joint.R_M_A, 0x0f);

        put(Joint.R_F_H, 0x0a);
        put(Joint.R_F_K, 0x0b);
        put(Joint.R_F_A, 0x0c);

    }});

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final InfoMgr infoMgr;
    private final ServoMgr servoMgr;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition waiter = lock.newCondition();
    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();


    @Inject
    public DefaultPostureMgr(Executor executor, InfoMgr infoMgr, ServoMgr servoMgr) {
        this.infoMgr = infoMgr;
        this.servoMgr = servoMgr;
        executor.execute(this);
    }


    @Override
    public void interrupt() {

        // 清空队列中的任务
        final Iterator<Task> taskIt = queue.iterator();
        while (taskIt.hasNext()) {
            taskIt.next().promise().tryCancel();
            taskIt.remove();
        }

        // 唤醒当前正在等待的任务
        lock.lock();
        try {
            waiter.signal();
        } finally {
            lock.unlock();
        }

        logger.info("{}/posture/interrupt", infoMgr.getName());

    }

    @Override
    public ListenableFuture<Void> change(Posture[] postures) {
        final Promise<Void> promise = new Promisor().promise();
        if (!queue.offer(new Task(postures, promise))) {
            promise.tryException(new Exception("posture queue is full!"));
        }
        return promise;
    }

    private record Task(Posture[] postures, Promise<Void> promise) {

    }

    private static int indexOf(Joint joint) {
        return mapping.get(joint);
    }

    private static ServoMgr.Position[] toPositions(Posture posture) {
        return posture.map().entrySet().stream()
                .map(entry -> new ServoMgr.Position(indexOf(entry.getKey()), raToPw(entry.getValue())))
                .toList()
                .toArray(new ServoMgr.Position[0]);
    }

    @Override
    public void run() {
        final String name = "%s/posture/worker".formatted(infoMgr.getName());
        final Thread current = Thread.currentThread();
        current.setName(name);
        logger.info("{} start", name);
        while (!current.isInterrupted()) {
            try {
                final Task task = queue.take();
                try {
                    boolean isCanceled = false;
                    for (final Posture posture : task.postures()) {
                        servoMgr.rotate(posture.getDuration(), toPositions(posture));
                        lock.lockInterruptibly();
                        try {
                            if (waiter.await(posture.getDuration(), TimeUnit.MILLISECONDS)) {
                                isCanceled = true;
                                break;
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                    if (isCanceled) {
                        task.promise().tryCancel();
                    } else {
                        task.promise().trySuccess();
                    }
                } catch (InterruptedException iCause) {
                    task.promise().tryCancel();
                    throw iCause;
                } catch (Exception cause) {
                    task.promise().tryException(cause);
                }
            } catch (InterruptedException iCause) {
                current.interrupt();
            }
        }// while
        logger.info("{} stopped!", name);
    }

}
