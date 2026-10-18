package dev.gurwi.prism.argument;

import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.command.CommandExecutor;
import dev.gurwi.prism.command.builder.ExecutorContainerBuilder;
import dev.gurwi.prism.command.builder.TreeBuilder;
import dev.gurwi.prism.permission.PermissionNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class AbstractTreeArgument<
        Impl extends AbstractTreeArgument<Impl, T, Definition, Source, Executor, Argument, Permission, Suggestions>,
        T,
        Definition extends AbstractCommandDefinition<Definition, Source, Executor, Argument, Permission>,
        Source,
        Executor extends CommandExecutor<? extends Source, Definition>,
        Argument extends AbstractArgument<?, ?, ? extends Source, ?>,
        Permission extends PermissionNode<?, Source>,
        Suggestions extends SuggestionsSupplier<Source, ?>
> extends AbstractArgument<Impl, T, Source, Suggestions> implements TreeBuilder<Impl, AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions>>, ExecutorContainerBuilder<Impl, Executor> {

    protected final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();
    protected final List<AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions>> arguments = new ArrayList<>();

    public AbstractTreeArgument(String name, Suggestions defaultSuggestions) {
        super(name, defaultSuggestions);
    }

    @Override
    public <E extends Executor> Impl executes(Class<E> type, E executor) {
        executors.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(executor, "executor"));
        return self();
    }

    @Override
    public Impl clearExecutors() {
        executors.clear();
        return self();
    }

    @Override
    public Impl then(@NotNull AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> next) {
        Objects.requireNonNull(next, "next");

        if (next.contains(this)) {
            throw new IllegalArgumentException("Argument '" + getName() + "' cannot be added below itself");
        }

        for (AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> existing : arguments) {
            if (existing.getName().equals(next.getName())) {
                throw new IllegalArgumentException("Argument '" + getName() + "' already has a child named '" + next.getName() + "'");
            }
        }

        arguments.add(next);
        return self();
    }

    @Override
    public final Impl then(@NotNull List<? extends AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions>> trees) {
        int length = trees.size();
        if (length == 0) {
            return self();
        }

        AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> combined =
                Objects.requireNonNull(trees.get(length - 1), "tree");

        for (int i = length - 2; i >= 0; i--) {
            AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> current =
                    Objects.requireNonNull(trees.get(i), "tree");
            current.then(combined);
            combined = current;
        }

        return then(combined);
    }

    public @NotNull @Unmodifiable List<AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions>> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public @NotNull @Unmodifiable Collection<Executor> getExecutors() {
        return Collections.unmodifiableCollection(executors.values());
    }

    private boolean contains(@NotNull AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> target) {
        if (this == target) {
            return true;
        }

        for (AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, Suggestions> child : arguments) {
            if (child.contains(target)) {
                return true;
            }
        }

        return false;
    }

}