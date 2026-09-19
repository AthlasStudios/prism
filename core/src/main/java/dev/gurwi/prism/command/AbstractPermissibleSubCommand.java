package dev.gurwi.prism.command;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.permission.PermissionNode;

public abstract class AbstractPermissibleSubCommand<Impl extends AbstractPermissibleSubCommand<Impl, P, E, A, Node>, P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>, Node extends PermissionNode<?, ?>> extends AbstractCommand<Impl, P, E, A> {

    protected AbstractPermissibleSubCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
