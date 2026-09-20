package dev.gurwi.prism.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ExecutionRequirement<Source> {

    boolean canExecute(Source source);

    @Contract(pure = true)
    static <S> @NotNull ExecutionRequirement<S> none() {
        return source -> true;
    }

}
