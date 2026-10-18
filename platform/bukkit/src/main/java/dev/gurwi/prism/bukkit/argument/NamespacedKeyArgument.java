package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NamespacedKeyArgument extends BukkitArgument<NamespacedKeyArgument, NamespacedKey> {

    public NamespacedKeyArgument(String name) {
        super(name);
    }

    @Override
    public NamespacedKey parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        return NamespacedKey.minecraft(value.toLowerCase());
    }

}