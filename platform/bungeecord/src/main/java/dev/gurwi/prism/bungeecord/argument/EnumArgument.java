package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumArgument<E extends Enum<E>> extends BungeecordArgument<EnumArgument<E>, E> {

    private final Class<E> enumClass;

    public EnumArgument(String name, @NotNull Class<E> enumClass) {
        super(name);

        this.enumClass = enumClass;

        List<String> values = Arrays.stream(enumClass.getEnumConstants())
                .map(value -> value.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    public E parse(@NotNull CommandSender source, @NotNull String value) throws ArgumentParseException {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected " + enumClass.getSimpleName() + " got " + value);
        }
    }

    @Override
    public ArgumentType<?> getArgumentType() {
        return StringArgumentType.word();
    }

}
