package dev.gurwi.prism.brigadier.minecraft.paper;

import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftCommandDispatcherProvider;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import org.bukkit.plugin.Plugin;

public class CommandDispatcherProvider extends MinecraftCommandDispatcherProvider {

    private final WrappedCommandDispatcher dispatcher;

    protected CommandDispatcherProvider(Plugin plugin) {
        super(plugin);
        this.dispatcher = new WrappedCommandDispatcher(plugin);
    }

    @Override
    public MinecraftCommandDispatcher<?> getDispatcher() {
        return dispatcher;
    }

}
