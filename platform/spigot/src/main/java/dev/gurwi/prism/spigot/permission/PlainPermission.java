package dev.gurwi.prism.spigot.permission;

import org.bukkit.command.CommandSender;

public class PlainPermission implements SpigotPermissionNode {

    private final String permission;

    public PlainPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean hasPermission(CommandSender source) {
        if (permission.isEmpty()) return true;
        return source.isOp() || source.hasPermission(permission);
    }

}
