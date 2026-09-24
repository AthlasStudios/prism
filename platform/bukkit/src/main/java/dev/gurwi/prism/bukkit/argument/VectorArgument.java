package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorArgument extends BukkitArgument<VectorArgument, Vector> {

    public VectorArgument(String name) {
        super(name);
    }

    @Override
    public Vector parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] parts = value.split(",");

        if (parts.length != 3) {
            throw new ArgumentParseException(this, value, "Expected a vector in format x,y,z got " + value);
        }

        try {
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);

            return new Vector(x, y, z);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected numeric vector coordinates got " + value);
        }
    }

}
