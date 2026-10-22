package dev.gurwi.prism.velocity.permission;

import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public enum CommandPermission implements VelocityPermission {

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
    public boolean hasPermission(@NotNull CommandSource source) {
        return VelocityPermission.of(permission).hasPermission(source);
    }

}
