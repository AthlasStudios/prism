package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractPermissibleCommand;
import dev.gurwi.prism.permission.PermissionNode;

public class PermissibleSubCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends PermissibleCommandDefinition<P, E, Node> {

    public PermissibleSubCommandDefinition(AbstractPermissibleCommand<?, P, E, Node> command) {
        super(command);
    }

}
