package dev.gurwi.prism.brigadier.minecraft.V1_19;

import com.mojang.brigadier.CommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.ReflectiveCommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WrappedCommandDispatcher extends ReflectiveCommandDispatcher<CommandSourceStack> {

    protected WrappedCommandDispatcher(@NotNull Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
    }

    @Override
    public MinecraftCommandSource toMinecraftSource(CommandSourceStack source) {
        return new WrappedSourceStack(source);
    }

    @Override
    public CommandDispatcher<CommandSourceStack> getVanillaDispatcher() {
        return MinecraftServer.getServer().getCommands().getDispatcher();
    }

}
