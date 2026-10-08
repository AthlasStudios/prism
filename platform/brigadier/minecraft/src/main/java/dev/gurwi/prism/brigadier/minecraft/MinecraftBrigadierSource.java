package dev.gurwi.prism.brigadier.minecraft;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface MinecraftBrigadierSource {

    CommandSender sender();

    Entity executor();

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull MinecraftBrigadierSource delegate(Supplier<CommandSender> sender, Supplier<Entity> executor) {
        return new DelegatingSource(sender, executor);
    }

    class DelegatingSource implements MinecraftBrigadierSource {

        private final Supplier<CommandSender> sender;
        private final Supplier<Entity> executor;

        private DelegatingSource(Supplier<CommandSender> sender, Supplier<Entity> executor) {
            this.sender = sender;
            this.executor = executor;
        }

        @Override
        public CommandSender sender() {
            return sender.get();
        }

        @Override
        public Entity executor() {
            return executor.get();
        }

    }

}
