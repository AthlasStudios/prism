package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WorldArgument extends BukkitArgument<WorldArgument, World> {

    public WorldArgument(String name) {
        super(name);

        replaceSuggestions(Bukkit.getWorlds()
                .stream()
                .map(World::getName)
                .toList()
        );
    }

    @Override
    public World parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        World world = Bukkit.getWorld(value);

        if (world == null) {
            throw new ArgumentParseException(this, value, "World not found: " + value);
        }

        return world;
    }

}
