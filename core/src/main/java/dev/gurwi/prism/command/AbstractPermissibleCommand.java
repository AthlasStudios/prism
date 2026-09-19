package dev.gurwi.prism.command;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.permission.PermissionNode;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPermissibleCommand<Impl extends AbstractPermissibleCommand<Impl, P, E, A, Node>, P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>, Node extends PermissionNode<?, ?>> extends AbstractCommand<Impl, P, E, A> {

    private final Set<Node> permissionNodes = new HashSet<>();

    protected AbstractPermissibleCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @SafeVarargs
    public final Impl withPermission(Node... permissions) {
        permissionNodes.addAll(Set.of(permissions));
        return instance();
    }

}
