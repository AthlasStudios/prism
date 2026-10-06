package dev.gurwi.prism.bukkit.command;

import java.util.List;

public class PrismBukkitCommand extends BukkitCommandBuilder<PrismBukkitCommand> {

    public PrismBukkitCommand(String name, List<String> aliases, String description) {
        super(name, aliases, description);
    }

    public PrismBukkitCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
