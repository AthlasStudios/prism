package dev.gurwi.prism.command.definition;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.permission.PermissionNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissibleSubCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E, A>>, A extends AbstractArgument<?, ?, ?>, E extends CommandExecutor<?>, Node extends PermissionNode<?, ?>> extends PermissibleCommandDefinition<P, E, A, Node> {

    public PermissibleSubCommandDefinition(String name, String[] aliases, List<A> requiredArguments, List<A> optionalArguments, Map<Class<? extends E>, E> executors, Set<Node> permissions) {
        super(name, aliases, requiredArguments, optionalArguments, executors, permissions);
    }

}
