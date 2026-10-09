package dev.gurwi.prism.brigadier.minecraft.paper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedCommandDispatcher extends MinecraftCommandDispatcher<CommandSourceStack> {

    protected WrappedCommandDispatcher(@NotNull Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
    }

    @Override
    public MinecraftCommandSource toMinecraftSource(CommandSourceStack source) {
        return new WrappedSourceStack(source);
    }

    @Override
    public CommandDispatcher<CommandSourceStack> getVanillaDispatcher() {
        return null;
    }

    @Override
    public Command createBukkitCommand(MinecraftCommandDispatcher<CommandSourceStack> own, LiteralCommandNode<CommandSourceStack> mirrored, String description, List<String> aliases) {
        return null;
    }

}
