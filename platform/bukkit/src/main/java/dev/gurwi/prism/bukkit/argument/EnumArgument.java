package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumArgument<E extends Enum<E>> extends BukkitArgument<EnumArgument<E>, E> {

    private final Class<E> enumClass;

    public EnumArgument(String name, @NotNull Class<E> enumClass) {
        super(name);

        this.enumClass = enumClass;
        List<String> values = Arrays.stream(enumClass.getEnumConstants())
                .map(value -> value.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        replaceSuggestions(values);
        withFixedValuesUnlessNative(values);
    }

    @Override
    public E parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected " + enumClass.getSimpleName() + " got " + value);
        }
    }

}
