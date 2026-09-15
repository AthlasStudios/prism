package dev.gurwi.prism;

import org.jetbrains.annotations.NotNull;

public interface Platform<R extends CommandsRegistry<?, ?, ?>> {

    @NotNull String getIdentifier();

    @NotNull R getRegistry();

}
