package dev.gurwi.prism.bukkit.brigadier;

import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BrigadierBootstrap {

    Optional<BukkitCommandRegistrar> create(@NotNull Plugin plugin);

}
