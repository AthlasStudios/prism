package dev.gurwi.prism.paper.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import dev.gurwi.prism.paper.PaperPlatform;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncTabCompleter implements Listener {

    private static final long TIMEOUT_MILLIS = 500;

    private final PaperPlatform platform;

    public AsyncTabCompleter(PaperPlatform platform) {
        this.platform = platform;
    }

    @EventHandler
    public void onAsyncTabComplete(@NotNull AsyncTabCompleteEvent event) {
        if (!event.isCommand()) return;
        if (!AsyncCommandSuggestEvent.hasListeners()) return;

        String buffer = event.getBuffer();

        if (!buffer.startsWith("/")) return;

        String[] parts = buffer.substring(1).split(" ", -1);

        if (parts.length < 2) return;

        String label = parts[0].toLowerCase(Locale.ROOT);
        int namespace = label.indexOf(':');

        if (namespace != -1) label = label.substring(namespace + 1);

        BukkitCommandDefinition definition = find(label);

        if (definition == null) return;

        AsyncCommandSuggestEvent suggestEvent = new AsyncCommandSuggestEvent(
                platform,
                definition,
                event.getSender(),
                buffer,
                new ArrayList<>(event.getCompletions())
        );

        Bukkit.getPluginManager().callEvent(suggestEvent);

        if (suggestEvent.isCancelled()) {
            event.setCompletions(List.of());
            event.setHandled(true);
            return;
        }

        Optional<CompletableFuture<List<String>>> future = suggestEvent.getFuture();

        if (future.isEmpty()) return;

        List<String> result;

        try {
            result = future.get().get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException | TimeoutException e) {
            return;
        }

        event.setCompletions(result == null ? List.of() : result);
        event.setHandled(true);
    }

    private @Nullable BukkitCommandDefinition find(@NotNull String label) {

        return platform.getCommandsRegistry().getRegisteredCommands().stream()
                .filter(command -> command.getName().equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }

}