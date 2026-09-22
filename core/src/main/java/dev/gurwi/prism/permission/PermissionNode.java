package dev.gurwi.prism.permission;

public interface PermissionNode<Permission, Source> {

    Permission getPermission();

    boolean hasPermission(Source source);

}
