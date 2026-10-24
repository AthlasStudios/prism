package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Axis;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class AxesArgument extends BukkitArgument<AxesArgument, Set<Axis>> {

    public AxesArgument(String name) {
        super(name);

        includeSuggestionsUnlessNative("x", "y", "z", "xy", "xz", "yz", "xyz");
    }

    @Override
    public Set<Axis> parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Set<Axis> axes = EnumSet.noneOf(Axis.class);

        if (value.isEmpty()) {
            throw new ArgumentParseException(this, value, "Expected axes got an empty value");
        }

        for (char c : value.toLowerCase().toCharArray()) {
            try {
                Axis axis = Axis.valueOf(String.valueOf(c).toUpperCase());

                if (!axes.add(axis)) {
                    throw new ArgumentParseException(this, value, "Duplicate axis in " + value);
                }
            } catch (IllegalArgumentException e) {
                throw new ArgumentParseException(this, value, "Expected axes got " + value);
            }
        }

        return axes;
    }

}