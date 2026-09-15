package dev.gurwi.prism;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ExecutionRequirement<S> {

    boolean test(S source);

    @Contract(pure = true)
    static @NotNull ExecutionRequirement<?> none() {
        return s -> true;
    }

}
