package dev.gurwi.prism.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UsageRequirement<Source> {

    boolean canExecute(Source source);

    default UsageRequirement<Source> and(UsageRequirement<Source> requirement) {
        return source -> this.canExecute(source) && requirement.canExecute(source);
    }

    @Contract(pure = true)
    static <S> @NotNull UsageRequirement<S> none() {
        return source -> true;
    }

}
