package dev.gurwi.prism.command.builder;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.command.CommandExecutor;
import dev.gurwi.prism.permission.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractTreeCommandBuilder<
        Impl extends AbstractTreeCommandBuilder<Impl, Definition, Source, Executor, Argument, Permission>,
        Definition extends AbstractCommandDefinition<Definition, Source, Executor, Argument, Permission>,
        Source,
        Executor extends CommandExecutor<? extends Source, Definition>,
        Argument extends AbstractArgument<?, ?, Source, ?>,
        Permission extends PermissionNode<?, Source>
> extends AbstractCommandBuilder<Impl, Definition, Source, Executor, Argument, Permission> implements TreeBuilder<Impl, AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, ?>> {

    protected final List<AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, ?>> argumentTreeBranches = new ArrayList<>();

    public AbstractTreeCommandBuilder(String name, List<String> aliases) {
        super(name, aliases);
    }

    public AbstractTreeCommandBuilder(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public Impl then(@NotNull AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, ?> next) {
        argumentTreeBranches.add(Objects.requireNonNull(next, "next"));
        return self();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public final Impl then(@NotNull List<? extends AbstractTreeArgument<?, ?, Definition, Source, Executor, Argument, Permission, ?>> trees) {
        int length = trees.size();
        if (length == 0) {
            return self();
        }

        AbstractTreeArgument combined = Objects.requireNonNull(trees.get(length - 1), "tree");

        for (int i = length - 2; i >= 0; i--) {
            AbstractTreeArgument current = Objects.requireNonNull(trees.get(i), "tree");
            current.then(combined);
            combined = current;
        }

        return (Impl) then(combined);
    }

}
