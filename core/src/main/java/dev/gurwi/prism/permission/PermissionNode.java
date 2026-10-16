package dev.gurwi.prism.permission;

public interface PermissionNode<Permission, Source> {

    Permission permission();

    boolean hasPermission(Source source);

}
