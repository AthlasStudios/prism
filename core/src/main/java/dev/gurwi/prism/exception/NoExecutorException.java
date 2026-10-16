package dev.gurwi.prism.exception;

import org.jetbrains.annotations.NotNull;

public class NoExecutorException extends IllegalStateException {

    public NoExecutorException(@NotNull Class<?> type) {
        super("No Executor available for type " + type.getSimpleName());
    }

}
