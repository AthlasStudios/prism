package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CharacterArgument extends BukkitArgument<CharacterArgument, Character> {

    public CharacterArgument(String name) {
        super(name);
    }

    @Override
    public Character parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        if (value.length() != 1) {
            throw new ArgumentParseException(this, value, "Expected a character got " + value);
        }

        return value.charAt(0);
    }

}
