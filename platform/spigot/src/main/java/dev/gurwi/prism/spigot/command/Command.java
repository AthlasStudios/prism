package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.BukkitCommandBuilder;

import java.util.List;

public class Command extends BukkitCommandBuilder<Command> {

    public Command(String name, List<String> aliases, String description) {
        super(name, aliases, description);
    }

    public Command(String name, String... aliases) {
        super(name, aliases);
    }

}
