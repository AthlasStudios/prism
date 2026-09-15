package dev.gurwi.prism.spigot.registrar;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandsRegistrar<E> {

    protected final JavaPlugin plugin;

    protected CommandsRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void register(E executableCommand);

    public abstract void unregister(E executableCommand);

}
