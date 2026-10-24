package dev.gurwi.prism.bungeecord.permission;

import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public enum CommandPermission implements BungeecordPermission {

    OP("*"),
    NONE("");

    private final String permission;

    CommandPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String permission() {
        return permission;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender source) {
        return BungeecordPermission.of(permission).hasPermission(source);
    }

}
