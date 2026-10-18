package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StringArgument extends BukkitArgument<StringArgument, String> {

    public StringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        return value;
    }

}
