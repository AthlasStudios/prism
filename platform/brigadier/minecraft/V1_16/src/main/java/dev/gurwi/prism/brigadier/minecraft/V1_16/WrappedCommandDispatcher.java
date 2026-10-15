package dev.gurwi.prism.brigadier.minecraft.V1_16;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.ReflectiveCommandDispatcher;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedCommandDispatcher extends ReflectiveCommandDispatcher<CommandListenerWrapper> {

    protected WrappedCommandDispatcher(@NotNull Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
    }

    @Override
    public MinecraftCommandSource toMinecraftSource(CommandListenerWrapper source) {
        return new WrappedSourceStack(source);
    }

    @Override
    public CommandDispatcher<CommandListenerWrapper> getVanillaDispatcher() {
        return MinecraftServer.getServer().getCommandDispatcher().a();
    }

    @Override
    public Command createBukkitCommand(MinecraftCommandDispatcher<CommandListenerWrapper> own, LiteralCommandNode<CommandListenerWrapper> mirrored, String description, List<String> aliases) {
        return new BrigadierBukkitCommand(own, mirrored, description, aliases);
    }

}
