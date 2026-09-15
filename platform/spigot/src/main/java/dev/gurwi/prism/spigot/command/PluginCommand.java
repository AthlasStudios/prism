package dev.gurwi.prism.spigot.command;

import org.bukkit.plugin.Plugin;

public abstract class PluginCommand<P extends Plugin> extends Command {

    private final P plugin;

    public PluginCommand(P plugin, String name, String... aliases) {
        super(name, aliases);
        this.plugin = plugin;
    }

    public abstract void setup();

}
