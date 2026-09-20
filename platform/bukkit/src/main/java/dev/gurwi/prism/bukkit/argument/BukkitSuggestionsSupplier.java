package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.SuggestionsSupplier;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface BukkitSuggestionsSupplier<T> extends SuggestionsSupplier.Sync<T, CommandSender> {

}
