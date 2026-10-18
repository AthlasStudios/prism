package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NumberRangeArgument<T extends Number & Comparable<T>> extends BukkitArgument<NumberRangeArgument<T>, RangedNumber<T>> {

    private final Function<String, T> parser;

    public NumberRangeArgument(String name, Function<String, T> parser) {
        super(name);
        this.parser = parser;
    }

    @Override
    public RangedNumber<T> parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] parts = value.split("\\.\\.", -1);

        if (parts.length > 2) {
            throw new ArgumentParseException(
                    this,
                    value,
                    "Expected a range between two numbers"
            );
        }

        try {
            if (parts.length == 1) {
                T number = parser.apply(parts[0]);
                return new RangedNumber<>(number, number);
            }

            T min = parts[0].isEmpty() ? null : parser.apply(parts[0]);
            T max = parts[1].isEmpty() ? null : parser.apply(parts[1]);

            return new RangedNumber<>(min, max);

        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(
                    this,
                    value,
                    e.getMessage()
            );
        }
    }

}
