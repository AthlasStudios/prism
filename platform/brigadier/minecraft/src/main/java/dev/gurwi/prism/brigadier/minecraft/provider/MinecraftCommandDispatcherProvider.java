package dev.gurwi.prism.brigadier.minecraft.provider;

import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import org.bukkit.plugin.Plugin;

public abstract class MinecraftCommandDispatcherProvider {

    protected final Plugin plugin;

    protected MinecraftCommandDispatcherProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract MinecraftCommandDispatcher<?> getDispatcher();

}