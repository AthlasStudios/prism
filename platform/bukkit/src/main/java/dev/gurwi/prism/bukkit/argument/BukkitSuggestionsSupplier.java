package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.SuggestionsSupplier;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface BukkitSuggestionsSupplier extends SuggestionsSupplier.Sync<String, CommandSender> {

    @Contract(pure = true)
    static @NotNull BukkitSuggestionsSupplier empty() {
        return source -> Collections.emptyList();
    }

    @Contract(pure = true)
    static @NotNull BukkitSuggestionsSupplier of(Collection<String> suggestions) {
        return source -> suggestions;
    }

    @Contract(pure = true)
    static @NotNull BukkitSuggestionsSupplier of(String... suggestions) {
        return of(List.of(suggestions));
    }

}
