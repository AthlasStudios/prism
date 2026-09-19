package dev.gurwi.prism.command.definition;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.permission.WithPermissions;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissibleCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>, Node extends PermissionNode<?, ?>> extends CommandDefinition<P, E, A> implements WithPermissions<Node> {

    private final Set<Node> permissions;

    public PermissibleCommandDefinition(String name, String[] aliases, List<A> requiredArguments, List<A> optionalArguments, Map<Class<? extends E>, E> executors, Set<Node> permissions) {
        super(name, aliases, requiredArguments, optionalArguments, executors);
        this.permissions = permissions;
    }

    @Override
    public @Unmodifiable Set<Node> getPermissions() {
        return Set.copyOf(permissions);
    }

}
