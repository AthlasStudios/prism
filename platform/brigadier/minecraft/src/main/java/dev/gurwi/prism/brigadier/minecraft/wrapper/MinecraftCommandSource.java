package dev.gurwi.prism.brigadier.minecraft.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public interface MinecraftCommandSource {

    CommandSender getSender();

    Optional<Entity> getEntity();

    Location getLocation();

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull MinecraftCommandSource create(Supplier<CommandSender> sender, Supplier<Entity> entity, Supplier<Location> location) {
        return new MinecraftCommandSource() {
            @Override
            public CommandSender getSender() {
                return sender.get();
            }

            @Override
            public Optional<Entity> getEntity() {
                return Optional.ofNullable(entity.get());
            }

            @Override
            public Location getLocation() {
                return location.get().clone();
            }
        };
    }

    static @NotNull MinecraftCommandSource create(Supplier<CommandSender> senderSupplier, Supplier<Entity> entitySupplier) {
        return create(senderSupplier, () -> null, () -> {
            Entity entity = entitySupplier.get();
            if (entity != null) return entity.getLocation();

            CommandSender sender = senderSupplier.get();
            if (sender instanceof LivingEntity livingEntity) return livingEntity.getLocation();
            if (sender instanceof BlockCommandSender blockCommandSender) return blockCommandSender.getBlock().getLocation().clone();

            return Bukkit.getWorlds().get(0).getSpawnLocation();
        });
    }

//    MinecraftCommandSource withLocation(@NotNull Location location);
//
//    MinecraftCommandSource withExecutor(@NotNull Entity executor);

}
