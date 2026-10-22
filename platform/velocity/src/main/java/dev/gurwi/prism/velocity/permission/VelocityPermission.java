package dev.gurwi.prism.velocity.permission;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import dev.gurwi.prism.permission.PermissionNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface VelocityPermission extends PermissionNode<String, CommandSource> {

    @Contract(value = "_ -> new", pure = true)
    static @NotNull VelocityPermission of(@NotNull String permission) {
        return new Plain(permission);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull VelocityPermission @NotNull [] of(@NotNull String... permissions) {
        return Arrays.stream(permissions).map(VelocityPermission::of).toArray(VelocityPermission[]::new);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull VelocityPermission startsWith(@NotNull String prefix, @NotNull Collection<String> knownPermissions) {
        return new StartsWith(prefix, knownPermissions);
    }

    record Plain(String permission) implements VelocityPermission {

        @Override
        public boolean hasPermission(@NotNull CommandSource source) {
            if (source instanceof ConsoleCommandSource) return true;
            return source.hasPermission(permission);
        }

    }

    class StartsWith implements VelocityPermission {

        private final String prefix;
        private final List<String> candidates;

        public StartsWith(@NotNull String prefix, @NotNull Collection<String> knownPermissions) {
            this.prefix = prefix.toLowerCase(Locale.ROOT);
            this.candidates = knownPermissions.stream()
                    .filter(permission -> permission.toLowerCase(Locale.ROOT).startsWith(this.prefix))
                    .toList();
        }

        @Override
        public String permission() {
            return prefix + "...";
        }

        @Override
        public boolean hasPermission(@NotNull CommandSource source) {
            if (source instanceof ConsoleCommandSource) return true;

            for (String candidate : candidates) {
                if (source.hasPermission(candidate)) return true;
            }

            return false;
        }

    }

}