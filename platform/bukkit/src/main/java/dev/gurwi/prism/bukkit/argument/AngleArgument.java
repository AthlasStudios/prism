package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.parser.RelativeValues;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AngleArgument extends BukkitArgument<AngleArgument, Float> {

    public AngleArgument(String name) {
        super(name);
    }

    @Override
    public Float parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return (float) RelativeValues.parse(value, RelativeValues.origin(sender).getYaw());
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected an angle got " + value);
        }
    }

}
