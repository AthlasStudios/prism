package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.CommandArguments;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor<Source, Definition extends AbstractCommandDefinition<Definition, ? super Source, ?, ?, ?>> {

    void run(Source source, CommandArguments<Definition, ? super Source> args);

    @Contract(pure = true)
    static <Source, Definition extends AbstractCommandDefinition<Definition, Source, ?, ?, ?>> @NotNull CommandExecutor<Source, Definition> empty() {
        return (source, args) -> {};
    }

}
