package dev.gurwi.prism.spigot.executable;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.Argument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SpigotExecutableCommand extends Command  {

    private final CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> commandDefinition;

    public SpigotExecutableCommand(@NotNull CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> commandDefinition) {
        super(commandDefinition.getName());
        this.commandDefinition = commandDefinition;

        setAliases(List.copyOf(commandDefinition.getAliases()));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        CommandArguments parsedArgs = parseArgs(args);
        commandDefinition.getExecutors().values().forEach(executor -> invoke(executor, sender, parsedArgs));

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        commandDefinition.getRequiredArguments().forEach(argument -> {
            getArgumentSuggestions(argument, sender);
        });
        return List.of();
    }

    public CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> getCommandDefinition() {
        return commandDefinition;
    }

    private <T extends CommandSender> void invoke(@NotNull SpigotCommandExecutor<T> executor, CommandSender sender, CommandArguments args) {
        if (!executor.getSourceType().isInstance(sender)) return;
        executor.run(executor.cast(sender), args);
    }

    private <S extends CommandSender> @Unmodifiable @NotNull List<String> getArgumentSuggestions0(@NotNull Argument<?, S> argument, @NotNull CommandSender sender) {
        CompletableFuture<? extends Collection<?>> suggestions = argument.getSuggestionsSupplier().get(sender);
        if (!suggestions.isDone()) throw new IllegalStateException("Spigot doesn't support async tab completions!");
        return suggestions.join().stream().map(Object::toString).toList();
    }

    @Contract("_ -> new")
    private @NotNull CommandArguments parseArgs(String[] args) {
        List<Argument<?, ?>> requiredArguments = commandDefinition.getRequiredArguments();
        List<Argument<?, ?>> optionalArguments = commandDefinition.getOptionalArguments();

        Map<String, Object> parsedRequiredArgs = new HashMap<>();
        Map<String, Object> parsedOptionalArgs = new HashMap<>();

        for (int i = 0; i <= requiredArguments.size() - 1; i++) {
            if ((args.length - 1) < i) break;

            Argument<?, ?> argument = requiredArguments.get(i);
            parsedRequiredArgs.put(argument.getName(), argument.parse(args, i));
        }

        for (int i = 0; i <= optionalArguments.size() - 1; i++) {
            int argToParseIndex = parsedRequiredArgs.size() + i;
            if ((args.length - 1) < argToParseIndex) break;

            Argument<?, ?> argument = optionalArguments.get(i);
            parsedOptionalArgs.put(argument.getName(), argument.parse(args, argToParseIndex));
        }

        return new CommandArguments(parsedRequiredArgs, parsedOptionalArgs);
    }

}
