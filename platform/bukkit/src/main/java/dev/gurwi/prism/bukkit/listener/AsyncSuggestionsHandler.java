package dev.gurwi.prism.bukkit.listener;

import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

public class AsyncSuggestionsHandler implements Listener {

    private final LegacyCommandRegistrar legacyRegistrar;

    public AsyncSuggestionsHandler(LegacyCommandRegistrar legacyRegistrar) {
        this.legacyRegistrar = legacyRegistrar;
    }

    @EventHandler
    public void onAsyncCommandSuggest(@NotNull AsyncCommandSuggestEvent event) {
        if (event.isHandled() || event.isCancelled()) return;

        String buffer = event.getBuffer();

        if (!buffer.startsWith("/")) return;

        String[] parts = buffer.substring(1).split(" ", -1);

        if (parts.length < 2) return;

        Command command = legacyRegistrar.getCommandMap().getCommand(parts[0].toLowerCase(Locale.ROOT));

        if (!(command instanceof BukkitExecutableCommand)) return;

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        ((BukkitExecutableCommand) command).completeAsync(event.getSender(), args).ifPresent(event::provideSuggestions);
    }

}