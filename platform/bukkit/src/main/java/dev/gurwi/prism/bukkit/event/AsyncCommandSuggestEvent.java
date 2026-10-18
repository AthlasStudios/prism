package dev.gurwi.prism.bukkit.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncCommandSuggestEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender player;
    private final String buffer;

    private CompletableFuture<List<String>> future;

    public AsyncCommandSuggestEvent(CommandSender player, String buffer) {
        this.player = player;
        this.buffer = buffer;
    }

    public boolean isHandled() {
        return future != null;
    }

    public void provideSuggestions(@NotNull CompletableFuture<List<String>> future) {
        this.future = future;
    }

    public void provideSuggestions(@NotNull List<String> suggestions) {
        this.future = CompletableFuture.completedFuture(suggestions);
    }

    public @NotNull CommandSender getPlayer() {
        return player;
    }

    public @NotNull String getBuffer() {
        return buffer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

}
