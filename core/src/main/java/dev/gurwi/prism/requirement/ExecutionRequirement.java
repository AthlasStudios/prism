package dev.gurwi.prism.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ExecutionRequirement<Source> {

    boolean canExecute(Source source);

    default ExecutionRequirement<Source> and(ExecutionRequirement<Source> requirement) {
        return source -> this.canExecute(source) && requirement.canExecute(source);
    }

    @Contract(pure = true)
    static <S> @NotNull ExecutionRequirement<S> none() {
        return source -> true;
    }

}
