package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.SuggestionSupplier;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class Argument<Impl, T> extends AbstractArgument<Impl, T, SuggestionSupplier<T, ? extends CommandSender>> {

    public Argument(String name) {
        super(name);
    }

    public abstract boolean validate(String rawArg);

    public abstract T parse(String @NotNull [] args, int index);

}
