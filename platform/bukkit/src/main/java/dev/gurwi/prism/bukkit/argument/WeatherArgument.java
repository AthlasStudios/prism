package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherArgument extends BukkitArgument<WeatherArgument, WeatherType> {

    public WeatherArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(WeatherType.values())
                .map(weatherType -> weatherType.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        replaceSuggestions(values);
        withFixedValuesUnlessNative(values);
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
