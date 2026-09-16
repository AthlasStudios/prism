package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractPermissibleCommand;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.permission.WithPermissions;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Set;

public class PermissibleCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends CommandDefinition<P, E> implements WithPermissions<Node> {

    private final Set<Node> permissions;

    public PermissibleCommandDefinition(String name, String[] aliases, Map<Class<? extends E>, E> executors, Set<Node> permissions) {
        super(name, aliases, executors);
        this.permissions = permissions;
    }

    @Override
    public @Unmodifiable Set<Node> getPermissions() {
        return Set.copyOf(permissions);
    }

}
