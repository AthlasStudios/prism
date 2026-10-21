package dev.gurwi.prism.brigadier.minecraft.V1_16;

import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.Vec2F;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class WrappedSourceStack implements MinecraftCommandSource {

    private final CommandListenerWrapper source;

    public WrappedSourceStack(CommandListenerWrapper source) {
        this.source = source;
    }

    @Override
    public Location getLocation() {
        Vec3D position = source.getPosition();
        Vec2F rotation = source.i();

        return new Location(source.getWorld().getWorld(), position.x, position.y, position.z, rotation.j, rotation.i);
    }

    @Override
    public CommandSender getSender() {
        return source.getBukkitSender();
    }

    @Override
    public Optional<Entity> getEntity() {
        net.minecraft.server.v1_16_R3.Entity entity = source.getEntity();

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity.getBukkitEntity());
    }

    public CommandListenerWrapper getHandle() {
        return source;
    }

}