package dev.gurwi.prism.brigadier.minecraft.V1_17;

import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftCommandDispatcherProvider;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import org.bukkit.plugin.Plugin;

public class CommandDispatcherProvider extends MinecraftCommandDispatcherProvider {

    private final MinecraftCommandDispatcher<?> dispatcher;

    protected CommandDispatcherProvider(Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
        dispatcher = new WrappedCommandDispatcher(plugin);
    }

    @Override
    public MinecraftCommandDispatcher<?> getDispatcher() {
        return dispatcher;
    }

}
