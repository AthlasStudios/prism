package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BooleanArgument extends BukkitArgument<BooleanArgument, Boolean> {

    public BooleanArgument(String name) {
        super(name);

        replaceSuggestions("true", "false");
    }

    @Override
    public Boolean parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }

        if (value.equalsIgnoreCase("false")) {
            return false;
        }

        throw new ArgumentParseException(this, value, "Expected a boolean got " + value);
    }

}
