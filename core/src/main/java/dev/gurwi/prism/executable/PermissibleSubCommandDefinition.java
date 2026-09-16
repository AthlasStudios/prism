package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractPermissibleCommand;
import dev.gurwi.prism.permission.PermissionNode;

import java.util.Map;
import java.util.Set;

public class PermissibleSubCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends PermissibleCommandDefinition<P, E, Node> {

    public PermissibleSubCommandDefinition(String name, String[] aliases, Map<Class<? extends E>, E> executors, Set<Node> permissions) {
        super(name, aliases, executors, permissions);
    }

}
