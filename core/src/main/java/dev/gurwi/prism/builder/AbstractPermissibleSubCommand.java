package dev.gurwi.prism.builder;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.permission.PermissionNode;

public abstract class AbstractPermissibleSubCommand<Impl extends AbstractPermissibleSubCommand<Impl, P, E, Node>, P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends AbstractCommand<Impl, P, E> {

    protected AbstractPermissibleSubCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
