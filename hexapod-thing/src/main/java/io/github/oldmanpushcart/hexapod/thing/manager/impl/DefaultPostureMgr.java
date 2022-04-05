package io.github.oldmanpushcart.hexapod.thing.manager.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.api.Posture;
import io.github.oldmanpushcart.hexapod.thing.manager.InfoMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.PostureMgr;
import io.github.oldmanpushcart.hexapod.thing.manager.ServoMgr;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.jpromisor.Promise;
import io.github.oldmanpushcart.jpromisor.Promisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.oldmanpushcart.hexapod.thing.manager.impl.ServoCodec.radianToPw;

@Singleton
public class DefaultPostureMgr implements PostureMgr, Runnable {

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

    private static Set<ServoMgr.Position> toPositions(Posture posture) {
        final Set<ServoMgr.Position> positions = new LinkedHashSet<>();
        posture.forEach((joint, radian) -> positions.add(new ServoMgr.Position(indexOf(joint), radianToPw(radian))));
        return Collections.unmodifiableSet(positions);
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

                            /*
                             * 计算等待时间，因为（串口通讯+机械设备运动）整体会让实际执行时间大于系统等待时间
                             * 所以这里必须进行修正以抹平，修正的方式采用固定指的方式
                             */
                            long timeoutMs = posture.getDuration() + 50;

                            if (waiter.await(timeoutMs, TimeUnit.MILLISECONDS)) {
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
