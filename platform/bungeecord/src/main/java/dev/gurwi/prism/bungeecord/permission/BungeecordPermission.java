package dev.gurwi.prism.bungeecord.permission;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import dev.gurwi.prism.permission.PermissionNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public interface BungeecordPermission extends PermissionNode<String, CommandSender> {

    @Contract(value = "_ -> new", pure = true)
    static @NotNull BungeecordPermission of(@NotNull String permission) {
        return new Plain(permission);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull BungeecordPermission @NotNull [] of(@NotNull String... permissions) {
        return Arrays.stream(permissions).map(BungeecordPermission::of).toArray(BungeecordPermission[]::new);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull BungeecordPermission startsWith(@NotNull String prefix, @NotNull Collection<String> knownPermissions) {
        return new StartsWith(prefix, knownPermissions);
    }

    final class Plain implements BungeecordPermission {

        private final String permission;

        public Plain(String permission) {
            this.permission = permission;
        }

        @Override
        public String permission() {
            return permission;
        }

        @Override
        public boolean hasPermission(@NotNull CommandSender source) {
            if (permission.isEmpty() || !(source instanceof ProxiedPlayer)) return true;
            return source.hasPermission(permission);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Plain)) return false;

            return Objects.equals(permission, ((Plain) o).permission);
        }

        @Override
        public int hashCode() {
            return Objects.hash(permission);
        }

        @Override
        public String toString() {
            return "Plain[permission=" + permission + "]";
        }

    }

    class StartsWith implements BungeecordPermission {

        private final String prefix;
        private final List<String> candidates;

        public StartsWith(@NotNull String prefix, @NotNull Collection<String> knownPermissions) {
            this.prefix = prefix.toLowerCase(Locale.ROOT);
            this.candidates = knownPermissions.stream()
                    .filter(permission -> permission.toLowerCase(Locale.ROOT).startsWith(this.prefix)).collect(Collectors.toUnmodifiableList());
        }

        @Override
        public String permission() {
            return prefix + "...";
        }

        @Override
        public boolean hasPermission(@NotNull CommandSender source) {
            if (!(source instanceof ProxiedPlayer)) return true;

            for (String candidate : candidates) {
                if (source.hasPermission(candidate)) return true;
            }

            return false;
        }

    }

}