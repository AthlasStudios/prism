package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.parser.RelativeValues;
import dev.gurwi.prism.bukkit.argument.type.Rotation;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RotationArgument extends MultiTokenArgument<RotationArgument, Rotation> {

    public RotationArgument(String name) {
        super(name, 2);
    }

    @Override
    public Rotation parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] tokens = RelativeValues.tokens(value);

        if (tokens.length != 2) {
            throw new ArgumentParseException(this, value, "Expected a rotation got " + value);
        }

        Location origin = RelativeValues.origin(sender);

        try {
            float yaw = (float) RelativeValues.parse(tokens[0], origin.getYaw());
            float pitch = (float) RelativeValues.parse(tokens[1], origin.getPitch());

            return new Rotation(yaw, pitch);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a rotation got " + value);
        }
    }

}