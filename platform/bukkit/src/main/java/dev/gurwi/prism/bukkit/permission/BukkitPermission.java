package dev.gurwi.prism.bukkit.permission;

import dev.gurwi.prism.permission.PermissionNode;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public interface BukkitPermission extends PermissionNode<String, CommandSender> {

    @Contract(value = "_ -> new", pure = true)
    static @NotNull BukkitPermission of(@NotNull String permission) {
        return new Plain(permission);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull BukkitPermission @NotNull [] of(@NotNull String... permissions) {
        return Arrays.stream(permissions).map(BukkitPermission::of).toArray(BukkitPermission[]::new);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull BukkitPermission startsWith(@NotNull String regex) {
        return new StartsWith(regex);
    }

    record Plain(String permission) implements BukkitPermission {

        @Override
            public boolean hasPermission(@NotNull CommandSender sender) {
                return sender.isOp() || sender.hasPermission(permission);
            }

        }

    class StartsWith implements BukkitPermission {

        private final String regex;

        public StartsWith(String regex) {
            this.regex = regex;
        }

        @Override
        public String permission() {
            return regex + "->...";
        }

        @Override
        public boolean hasPermission(CommandSender source) {
            if (CommandPermission.OP.hasPermission(source)) return true;

            for (PermissionAttachmentInfo effectivePermission : source.getEffectivePermissions()) {
                if (effectivePermission.getPermission().startsWith(regex.toLowerCase())) return true;
            }

            return false;
        }

    }

}
