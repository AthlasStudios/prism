package dev.gurwi.prism.bukkit.event;

import dev.gurwi.prism.bukkit.BukkitPlatform;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncCommandSuggestEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final BukkitPlatform<?> platform;
    private final BukkitCommandDefinition definition;
    private final CommandSender sender;
    private final String buffer;
    private final List<String> completions;

    private boolean cancelled = false;
    private CompletableFuture<List<String>> future;

    public AsyncCommandSuggestEvent(BukkitPlatform<?> platform, BukkitCommandDefinition definition, CommandSender sender, String buffer, List<String> completions) {
        super(true);

        this.platform = platform;
        this.definition = definition;
        this.sender = sender;
        this.buffer = buffer;
        this.completions = completions;
    }

    public void provideSuggestions(@NotNull CompletableFuture<List<String>> future) {
        this.future = future;
    }

    public void provideSuggestions(@NotNull List<String> suggestions) {
        this.future = CompletableFuture.completedFuture(suggestions);
    }

    public boolean isHandled() {
        return future != null;
    }

    public Optional<CompletableFuture<List<String>>> getFuture() {
        return Optional.ofNullable(future);
    }

    public BukkitPlatform<?> getPlatform() {
        return platform;
    }

    public BukkitCommandDefinition getDefinition() {
        return definition;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getBuffer() {
        return buffer;
    }

    public List<String> getCompletions() {
        return completions;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static boolean hasListeners() {
        return getHandlerList().getRegisteredListeners().length > 0;
    }

}