package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.MultiTokenArgument;
import org.jetbrains.annotations.NotNull;

public final class ArgumentTokens {

    private ArgumentTokens() {
    }

    public static int consumption(@NotNull BukkitArgument<?, ?> argument) {
        return argument instanceof MultiTokenArgument<?, ?> multi ? multi.getConsumption() : 1;
    }

    public static int advance(@NotNull BukkitArgument<?, ?> argument, int position, int length) {
        return argument instanceof GreedyArgument<?, ?> ? length : position + consumption(argument);
    }

}