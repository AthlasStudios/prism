package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LocationArgument extends BukkitArgument<LocationArgument, Location> {

    public LocationArgument(String name) {
        super(name);
    }

    @Override
    public Location parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] parts = value.split(",");

        if (parts.length != 4) {
            throw new ArgumentParseException(this, value, "Expected a location in format world,x,y,z got " + value);
        }

        World world = Bukkit.getWorld(parts[0]);

        if (world == null) {
            throw new ArgumentParseException(this, value, "World not found: " + parts[0]);
        }

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected numeric location coordinates got " + value);
        }
    }

}
