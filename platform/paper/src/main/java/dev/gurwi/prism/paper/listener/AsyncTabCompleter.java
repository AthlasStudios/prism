package dev.gurwi.prism.paper.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AsyncTabCompleter implements Listener {

    @EventHandler
    public void onAsyncTabComplete(@NotNull AsyncTabCompleteEvent event) {
        // TODO -> MAKE WRAPPER BETTER
        Bukkit.getPluginManager().callEvent(new AsyncCommandSuggestEvent(event.getSender(), event.getBuffer()));
    }

}
