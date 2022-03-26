package io.github.oldmanpushcart.hexapod.thing.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Initializer {

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    public boolean isInit() {
        return isInit.get();
    }

    public void init(Executable executable) {
        init(true, executable);
    }

    public void init(Supplier<Boolean> supplier, Executable executable) {
        init(supplier.get(), executable);
    }

    public void init(boolean condition, Executable executable) {
        if (!condition || !isInit.compareAndSet(false, true)) {
            return;
        }
        try {
            executable.execute();
        } catch (Exception cause) {
            isInit.compareAndSet(true, false);
        }
    }

    public interface Executable {

        void execute() throws Exception;

    }

}
