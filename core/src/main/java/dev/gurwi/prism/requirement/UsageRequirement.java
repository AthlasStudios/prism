package dev.gurwi.prism.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UsageRequirement<Source> {

    boolean test(Source source);

    default UsageRequirement<Source> and(UsageRequirement<Source> requirement) {
        return source -> this.test(source) && requirement.test(source);
    }

    @Contract(pure = true)
    static <S> @NotNull UsageRequirement<S> none() {
        return source -> true;
    }

}
