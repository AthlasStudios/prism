package dev.gurwi.prism.bukkit.argument.suggestion;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import org.bukkit.command.CommandSender;

public class BukkitSuggestionContext extends SuggestionContext<CommandSender> {

    public BukkitSuggestionContext(CommandSender commandSender, String currentInput, String currentRawArg, CommandArguments<?, CommandSender> previousArgs) {
        super(commandSender, currentInput, currentRawArg, previousArgs);
    }

}
