package dev.gurwi.prism.spigot.permission;

import dev.gurwi.prism.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SpigotPermissionNode extends PermissionNode<String, CommandSender> {

    @Contract(value = "_ -> new", pure = true)
    static @NotNull SpigotPermissionNode of(String permission) {
        return new PlainPermission(permission);
    }

    static @NotNull SpigotPermissionNode startsWith(String regex) {
        return new StartsWithPermission(regex);
    }

    default void register() {
        Bukkit.getPluginManager().addPermission(new Permission(getPermission()));
    }

}
