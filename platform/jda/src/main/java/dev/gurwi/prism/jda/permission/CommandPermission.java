package dev.gurwi.prism.jda.permission;

import dev.gurwi.prism.permission.PermissionNode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

public class CommandPermission implements PermissionNode<Permission, User> {

    @Override
    public Permission permission() {
        return null;
    }

    @Override
    public boolean hasPermission(User user) {
        return false;
    }

}
