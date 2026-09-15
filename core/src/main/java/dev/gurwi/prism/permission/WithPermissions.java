package dev.gurwi.prism.permission;

import java.util.Set;

public interface WithPermissions<P extends PermissionNode<?, ?>> {

    Set<P> getPermissions();

}
