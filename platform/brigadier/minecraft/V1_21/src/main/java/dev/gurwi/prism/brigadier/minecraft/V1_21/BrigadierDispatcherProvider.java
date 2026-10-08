package dev.gurwi.prism.brigadier.minecraft.V1_21;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.AbstractBrigadierProvider;
import dev.gurwi.prism.brigadier.minecraft.MinecraftBrigadierSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Function;

public class BrigadierDispatcherProvider extends AbstractBrigadierProvider<CommandSourceStack> {

    protected BrigadierDispatcherProvider(Plugin plugin, Function<CommandSourceStack, MinecraftBrigadierSource> wrapper) {
        super(plugin, wrapper);
    }

    @Override
    protected CommandDispatcher<CommandSourceStack> vanillaDispatcher() {
        return MinecraftServer.getServer().getCommands().getDispatcher();
    }

    @Override
    protected CommandMap commandMap() {
        return ((CraftServer) plugin.getServer()).getCommandMap();
    }

    @Override
    protected Command createCommand(CommandDispatcher<MinecraftBrigadierSource> own, LiteralCommandNode<CommandSourceStack> mirrored, String description, List<String> aliases) {
        return new VanillaCommand(own, mirrored, description, aliases);
    }

}
