package dev.gurwi.prism.brigadier.minecraft.V1_17;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.ReflectiveCommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    public Command createBukkitCommand(MinecraftCommandDispatcher<CommandSourceStack> own, LiteralCommandNode<CommandSourceStack> mirrored, String description, List<String> aliases) {
        return new BrigadierBukkitCommand(own, mirrored, description, aliases);
    }

}
