package dev.gurwi.prism.bukkit.permission;

import org.bukkit.command.CommandSender;

public enum CommandPermission implements BukkitPermission {

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
    public boolean hasPermission(CommandSender sender) {
        return BukkitPermission.of(permission).hasPermission(sender);
    }

}
