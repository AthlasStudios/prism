package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractPermissibleCommand;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.permission.WithPermissions;

import java.util.Set;

public class PermissibleCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends CommandDefinition<P, E> implements WithPermissions<Node> {

    public PermissibleCommandDefinition(AbstractPermissibleCommand<?, P, E, Node> command) {
        super(command);
    }

    @Override
    public Set<Node> getPermissions() {
        return Set.of();
    }

}
