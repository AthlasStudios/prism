package dev.gurwi.prism.brigadier.minecraft.paper;

import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WrappedSourceStack implements MinecraftCommandSource {

    private final CommandSourceStack sourceStack;

    public WrappedSourceStack(CommandSourceStack sourceStack) {
        this.sourceStack = sourceStack;
    }

    @Override
    public CommandSender getSender() {
        return sourceStack.getSender();
    }

    @Override
    public Optional<Entity> getEntity() {
        return Optional.ofNullable(sourceStack.getExecutor());
    }

    @Override
    public Location getLocation() {
        return sourceStack.getLocation();
    }

}
