package dev.gurwi.prism.brigadier.minecraft.V1_19;

import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class WrappedSourceStack implements MinecraftCommandSource {

    private final CommandSourceStack source;

    public WrappedSourceStack(CommandSourceStack source) {
        this.source = source;
    }

    @Override
    public Location getLocation() {
        Vec3 position = source.getPosition();
        Vec2 rotation = source.getRotation();

        return new Location(source.getLevel().getWorld(), position.x, position.y, position.z, rotation.y, rotation.x);
    }

    @Override
    public CommandSender getSender() {
        return source.getBukkitSender();
    }

    @Override
    public Optional<Entity> getEntity() {
        net.minecraft.world.entity.Entity entity = source.getEntity();

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity.getBukkitEntity());
    }

    public CommandSourceStack getHandle() {
        return source;
    }

//    @Override
//    public MinecraftCommandSource withLocation(@NotNull Location location) {
//        CommandSourceStack stack = source;
//
//        if (location.getWorld() instanceof CraftWorld world) {
//            stack = stack.withLevel(world.getHandle());
//        }
//
//        stack = stack.withPosition(new Vec3(location.getX(), location.getY(), location.getZ()));
//        stack = stack.withRotation(new Vec2(location.getPitch(), location.getYaw()));
//
//        return new WrappedSourceStack(stack);
//    }
//
//    @Override
//    public MinecraftCommandSource withExecutor(@NotNull Entity executor) {
//        return new WrappedSourceStack(source.withEntity(((CraftEntity) executor).getHandle()));
//    }

}