package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class WeatherArgument extends BukkitArgument<WeatherArgument, WeatherType> {

    public WeatherArgument(String name) {
        super(name);

        replaceSuggestions(
                Arrays.stream(WeatherType.values())
                        .map(weatherType -> weatherType.name().toLowerCase())
                        .toList()
        );
    }

    @Override
    public WeatherType parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return WeatherType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a weather type got " + value);
        }
    }

}
