package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LiteralArgument extends BukkitArgument<LiteralArgument, Void> {

    // TODO -> ADD ALIASES?
    // TODO -> ADD DESCRIPTION

    public LiteralArgument(String name) {
        super(name);
        super.listed = false;
    }

    @Override
    public Void parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        return null;
    }

    @Override
    public LiteralArgument setListed(boolean listed) {
        throw new IllegalArgumentException("Literal arguments cannot be listed");
    }

}
