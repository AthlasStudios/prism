package dev.gurwi.prism.spigot.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class StartsWithPermission implements SpigotPermissionNode {

    private final String regex;

    public StartsWithPermission(String regex) {
        this.regex = regex;
    }

    @Override
    public String getPermission() {
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
