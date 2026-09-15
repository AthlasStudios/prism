package dev.gurwi.prism.spigot.permission;

import org.bukkit.command.CommandSender;

public enum CommandPermission implements SpigotPermissionNode {

    OP("*"),
    NONE("");

    private final String permission;

    CommandPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean hasPermission(CommandSender source) {
        return SpigotPermissionNode.of(permission).hasPermission(source);
    }

}
