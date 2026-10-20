package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.base.MultiTokenArgument;
import org.jetbrains.annotations.NotNull;

public final class ArgumentTokens {

    private ArgumentTokens() {
    }

    public static int consumption(@NotNull BukkitArgument<?, ?> argument) {
        return argument instanceof MultiTokenArgument<?, ?> ? ((MultiTokenArgument<?, ?>) argument).getConsumption() : 1;
    }

    public static int advance(@NotNull BukkitArgument<?, ?> argument, int position, int length) {
        return argument instanceof GreedyArgument<?, ?> ? length : position + consumption(argument);
    }

}