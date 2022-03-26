package io.github.oldmanpushcart.hexapod.thing.util;

import java.util.function.Supplier;

public class CheckUtils {

    public static void checkArgument(boolean test, String message) {
        check(test, () -> new IllegalArgumentException(message));
    }

    public static void check(boolean test, Supplier<RuntimeException> exceptionFn) {
        if (!test) {
            throw exceptionFn.get();
        }
    }

}
