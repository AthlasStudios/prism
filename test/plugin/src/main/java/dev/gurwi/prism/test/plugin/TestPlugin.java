package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.Argument;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.Command;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .disableBrigadier()
                .init();

        CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> prismCommand = new Command("prism", "pr")
                .withArguments(new StringArgument("arg1"), new IntegerArgument("arg2"))
                .executes(((sender, args) -> {
                    String arg1 = args.get("arg1");
                    Integer arg2 = args.get("arg2");

                    sender.sendMessage("arg1: " + arg1);
                    sender.sendMessage("arg2: " + arg2);
                }))
                .register(platform);

        new Command("unregister")
                .withArguments(
                        new StringArgument("command")
                                .replaceSuggestions(platform.getRegistry().getRegisteredCommands().stream().map(CommandDefinition::getName).collect(Collectors.toList()))
                )
                .executes(((sender, args) -> {
                    String commandId = args.get("command");
                    platform.getRegistry().getRegisteredCommands().stream().filter(def -> def.getName().equals(commandId)).findFirst().ifPresent(def -> def.unregister(platform));
                    sender.sendMessage("Command unregistered");
                }))
                .register(platform);

        new Command("unregisterAll")
                .executes((sender, args) -> {
                    Set<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>>> unregisteredCommands = new HashSet<>();
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

        new Command("test")
                .executes(CustomExecutor.class, ((source, args) -> {
                    source.sendMessage("Hello world");
                }))
                .executesPlayer((player, args) -> {
                    player.sendMessage("Hello player");
                })
                .executesConsole((console, args) -> {
                    console.sendMessage("Hello console");
                })
                .register(platform);
    }

}
