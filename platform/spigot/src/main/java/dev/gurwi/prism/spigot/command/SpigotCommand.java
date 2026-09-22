package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.BukkitCommandBuilder;

import java.util.List;

public class SpigotCommand extends BukkitCommandBuilder<SpigotCommand> {

    public SpigotCommand(String name, List<String> aliases, String description) {
        super(name, aliases, description);
    }

    public SpigotCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
