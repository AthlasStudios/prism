package dev.gurwi.prism.spigot;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitPlatform;

public class SpigotPlatform implements BukkitPlatform<SpigotCommandsRegistry> {

    @Override
    public String getIdentifier() {
        return Prism.SPIGOT_PLATFORM_IDENTIFIER;
    }

    @Override
    public SpigotCommandsRegistry getCommandsRegistry() {
        return new SpigotCommandsRegistry();
    }

}
