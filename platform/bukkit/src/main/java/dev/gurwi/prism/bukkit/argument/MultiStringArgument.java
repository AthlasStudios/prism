package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.MultiTokenArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MultiStringArgument extends MultiTokenArgument<MultiStringArgument, String> {

    public MultiStringArgument(String name, int consumption) {
        super(name, consumption);
    }

    @Override
    public String parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        return value;
    }

}
