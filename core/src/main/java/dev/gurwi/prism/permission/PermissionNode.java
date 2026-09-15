package dev.gurwi.prism.permission;

public interface PermissionNode<P, S> {

    P getPermission();

    boolean hasPermission(S source);

}
