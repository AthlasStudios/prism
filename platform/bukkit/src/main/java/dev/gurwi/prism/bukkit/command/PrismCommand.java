package dev.gurwi.prism.bukkit.command;

import java.util.List;

public class PrismCommand extends BukkitCommandBuilder<PrismCommand> {

    public PrismCommand(String name, List<String> aliases) {
        super(name, aliases);
    }

    public PrismCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
