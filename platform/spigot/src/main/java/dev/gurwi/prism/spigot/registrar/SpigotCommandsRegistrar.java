package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.reflection.ReflectionUtil;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executable.SpigotExecutableCommand;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpigotCommandsRegistrar implements CommandsRegistrar {

    private final Map<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>, SpigotExecutableCommand> registeredCommands = new HashMap<>();

    private final JavaPlugin plugin;
    private final CommandMap commandMap;
    private final Map<String, Command> knownCommands;

    public SpigotCommandsRegistrar(@NotNull JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        this.plugin = plugin;
        this.commandMap = ReflectionUtil.accessField(plugin.getServer(), "commandMap");
        this.knownCommands = ReflectionUtil.accessFieldFromType(SimpleCommandMap.class, commandMap, "knownCommands");
    }

    @Override
    public void register(@NotNull CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition) {
        SpigotExecutableCommand command = new SpigotExecutableCommand(definition);
        commandMap.register(plugin.getName(), command);
        registeredCommands.put(definition, command);
    }

    @Override
    public void unregister(@NotNull CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition) {
        registeredCommands.get(definition).unregister(commandMap);
        knownCommands.entrySet().removeIf(entry -> entry.getValue().equals(registeredCommands.get(definition)));
        registeredCommands.remove(definition);
    }

    @Override
    public @Unmodifiable List<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> getRegisteredCommands() {
        return registeredCommands.values().stream().map(SpigotExecutableCommand::getCommandDefinition).toList();
    }

}
