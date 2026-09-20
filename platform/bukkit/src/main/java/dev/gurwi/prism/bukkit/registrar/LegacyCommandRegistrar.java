package dev.gurwi.prism.bukkit.registrar;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LegacyCommandRegistrar implements BukkitCommandRegistrar {

    private final Map<BukkitCommandDefinition, BukkitExecutableCommand> registeredCommands = new HashMap<>();

    private final JavaPlugin plugin;
    private final CommandMap commandMap;
    private final Map<String, Command> knownCommands;

    public LegacyCommandRegistrar(@NotNull JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        this.plugin = plugin;
        this.commandMap = ReflectionUtil.accessField(plugin.getServer(), "commandMap");
        this.knownCommands = ReflectionUtil.accessFieldFromType(SimpleCommandMap.class, commandMap, "knownCommands");
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        BukkitExecutableCommand command = new BukkitExecutableCommand(definition);
        commandMap.register(plugin.getName(), command);
        registeredCommands.put(definition, command);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        registeredCommands.get(definition).unregister(commandMap);
        knownCommands.entrySet().removeIf(entry -> entry.getValue().equals(registeredCommands.get(definition)));
        registeredCommands.remove(definition);
    }

    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Set.copyOf(registeredCommands.keySet());
    }

}
