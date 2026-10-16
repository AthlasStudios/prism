package dev.gurwi.prism.command.builder;

import dev.gurwi.prism.command.CommandExecutor;

public interface ExecutorContainerBuilder<Impl, Executor extends CommandExecutor<?, ?>> {

    <E extends Executor> Impl executes(Class<E> type, E executor);

    Impl clearExecutors();

}
