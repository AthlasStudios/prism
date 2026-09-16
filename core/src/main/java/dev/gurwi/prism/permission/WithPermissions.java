package dev.gurwi.prism.permission;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface WithPermissions<P extends PermissionNode<?, ?>> {

    @Unmodifiable
    Set<P> getPermissions();

}
