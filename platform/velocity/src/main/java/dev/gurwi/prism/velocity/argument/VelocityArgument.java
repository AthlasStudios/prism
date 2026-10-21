package dev.gurwi.prism.velocity.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import dev.gurwi.prism.velocity.executor.VelocityCommandExecutor;
import dev.gurwi.prism.velocity.executor.VelocityExecutorContainerBuilder;
import dev.gurwi.prism.velocity.permission.VelocityPermission;
import dev.gurwi.prism.velocity.requirement.VelocityUsageRequirementContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class VelocityArgument<Impl extends VelocityArgument<Impl, T>, T> extends AbstractTreeArgument<
        Impl,
        T,
        VelocityCommandDefinition,
        CommandSource,
        VelocityCommandExecutor<?>,
        VelocityArgument<?, ?>,
        VelocityPermission,
        SuggestionsSupplier<CommandSource, CompletableFuture<Collection<ArgumentSuggestion>>>
> implements VelocityExecutorContainerBuilder<Impl>, VelocityUsageRequirementContainer<Impl> {

    public VelocityArgument(String name) {
        super(name, SuggestionsSupplier.empty());
    }

    public boolean hasExecutor(@NotNull CommandSource source) {
        return executors.values().stream().anyMatch(executor -> executor.getSourceType().isInstance(source));
    }

    public abstract ArgumentType<T> getArgumentType();

}
