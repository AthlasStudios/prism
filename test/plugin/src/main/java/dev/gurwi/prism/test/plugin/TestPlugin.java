package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.Command;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .disableBrigadier()
                .init();

        CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> prismCommand = new Command("prism", "pr")
                .executes(((sender, args) -> sender.sendMessage("Hello World")))
                .register(platform);

        new Command("unregister")
                .executes(((sender, args) -> {
                    prismCommand.unregister(platform);
                    sender.sendMessage("Command unregistered");
                }))
                .register(platform);

        new Command("unregisterAll")
                .executes((sender, args) -> {
                    Set<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> unregisteredCommands = new HashSet<>();
                    platform.getRegistry().getRegisteredCommands().forEach(def -> {
                        def.unregister(platform);
                        unregisteredCommands.add(def);
                    });
                    sender.sendMessage("All commands unregistered");

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        unregisteredCommands.forEach(def -> def.register(platform));
                        sender.sendMessage("All commands registered back");
                    }, 20L * 3);
                })
                .register(platform);
    }

}
