package dev.gurwi.prism.bukkit.command;

import java.util.List;

public class PrismBukkitCommand extends BukkitCommandBuilder<PrismBukkitCommand> {

    public PrismBukkitCommand(String name, List<String> aliases) {
        super(name, aliases);
    }

    public PrismBukkitCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
