package dev.gurwi.prism.bukkit.registrar;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.bukkit.wrapper.WrappedCommandMap;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.command.Command;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LegacyCommandRegistrar implements BukkitCommandRegistrar {

    private final Map<BukkitCommandDefinition, Command> registeredCommands = new HashMap<>();

    private final JavaPlugin plugin;
    private final WrappedCommandMap commandMap;
    private final Map<String, Map<String, Object>> pluginCommands;

    public LegacyCommandRegistrar(@NotNull JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        this.plugin = plugin;
        this.commandMap = new WrappedCommandMap(plugin.getServer());
        this.pluginCommands = getPluginCommands();
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        BukkitExecutableCommand command = new BukkitExecutableCommand(plugin, definition);

        commandMap.register(plugin.getName(), command);
        addPluginCommand(definition);
        registeredCommands.put(definition, command);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        Command command = registeredCommands.remove(definition);
        if (command == null) return;

        command.unregister(commandMap);

        commandMap.getKnownCommands().entrySet().removeIf(entry -> entry.getValue() == command);
        pluginCommands.remove(definition.getName());
    }

    private void addPluginCommand(@NotNull BukkitCommandDefinition definition) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("description", definition.getDescription());

        if (!definition.getAliases().isEmpty()) {
            metadata.put("aliases", definition.getAliases());
        }

        pluginCommands.put(definition.getName(), metadata);
    }

    private @NotNull Map<String, Map<String, Object>> getPluginCommands() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Map<String, Object>> commands = new HashMap<>(
                ReflectionUtil.accessFieldFromType(
                        PluginDescriptionFile.class,
                        plugin.getDescription(),
                        "commands"
                )
        );

        ReflectionUtil.setField(
                plugin.getDescription(),
                "commands",
                commands
        );

        return commands;
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Set.copyOf(registeredCommands.keySet());
    }

    @Override
    public Type getType() {
        return Type.LEGACY;
    }

}