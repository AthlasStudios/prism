package dev.gurwi.prism.command.builder;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public interface TreeBuilder<Impl, T> {

    Impl then(@NotNull T next);

    Impl then(@NotNull List<? extends T> trees);

    @SuppressWarnings("unchecked")
    default Impl then(T... trees) {
        return then(Arrays.asList(trees));
    }

}
