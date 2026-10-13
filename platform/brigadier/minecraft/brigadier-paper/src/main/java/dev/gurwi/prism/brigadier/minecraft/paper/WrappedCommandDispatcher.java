package dev.gurwi.prism.brigadier.minecraft.paper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WrappedCommandDispatcher extends MinecraftCommandDispatcher<CommandSourceStack> {

    private final List<Entry> entries = new CopyOnWriteArrayList<>();

    protected WrappedCommandDispatcher(@NotNull Plugin plugin) {
        super(plugin);

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();

            for (Entry entry : entries) {
                String name = entry.root().getName();

                try {
                    registrar.register(mirror(entry.root(), name), entry.description(), entry.aliases());
                } catch (Throwable throwable) {
                    report("paper register " + name, throwable);
                }
            }
        });
    }

    @Override
    public MinecraftCommandSource toMinecraftSource(CommandSourceStack source) {
        return new WrappedSourceStack(source);
    }

    @Override
    public void register(@NotNull LiteralCommandNode<MinecraftCommandSource> node, String description, @NotNull List<String> aliases) {
        try {
            getRoot().addChild(node);

            LiteralCommandNode<MinecraftCommandSource> root = (LiteralCommandNode<MinecraftCommandSource>) getRoot().getChild(node.getName());

            entries.removeIf(entry -> entry.root() == root);
            entries.add(new Entry(root, description, List.copyOf(aliases)));
        } catch (Throwable throwable) {
            report("register " + node.getName(), throwable);
        }
    }

    private record Entry(LiteralCommandNode<MinecraftCommandSource> root, String description, List<String> aliases) {
    }

}
