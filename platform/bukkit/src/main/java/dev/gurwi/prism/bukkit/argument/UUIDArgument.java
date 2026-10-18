package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDArgument extends BukkitArgument<UUIDArgument, UUID> {

    public UUIDArgument(String name) {
        super(name);
    }

    @Override
    public UUID parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a UUID got " + value);
        }
    }

}
