package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TimeArgument extends BukkitArgument<TimeArgument, Integer> {

    private final int min;

    public TimeArgument(String name, int min) {
        super(name);

        this.min = min;
    }

    public TimeArgument(String name) {
        this(name, 0);
    }

    @Override
    public Integer parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String number = value;
        float multiplier = 1;

        if (value.endsWith("d")) {
            number = value.substring(0, value.length() - 1);
            multiplier = 24000;
        } else if (value.endsWith("s")) {
            number = value.substring(0, value.length() - 1);
            multiplier = 20;
        } else if (value.endsWith("t")) {
            number = value.substring(0, value.length() - 1);
        }

        int ticks;

        try {
            ticks = Math.round(Float.parseFloat(number) * multiplier);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a time got " + value);
        }

        if (ticks < min) {
            throw new ArgumentParseException(this, value, "Expected a time of at least " + min + " ticks got " + value);
        }

        return ticks;
    }

    public int getMin() {
        return min;
    }

}