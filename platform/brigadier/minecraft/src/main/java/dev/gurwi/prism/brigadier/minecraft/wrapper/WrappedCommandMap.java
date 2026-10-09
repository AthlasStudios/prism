package dev.gurwi.prism.brigadier.minecraft.wrapper;

import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.util.Map;

public class WrappedCommandMap {

    private final Server server;
    private final CommandMap commandMap;
    private final Map<String, Command> knownCommands;

    public WrappedCommandMap(Server server) throws NoSuchFieldException, IllegalAccessException {
        this.server = server;
        this.commandMap = ReflectionUtil.accessField(
                server,
                "commandMap"
        );

        knownCommands = ReflectionUtil.accessFieldFromType(
                SimpleCommandMap.class,
                commandMap,
                "knownCommands"
        );
    }

    public void register(String prefix, Command command) {
        commandMap.register(prefix, command);
    }

    public Map<String, Command> getKnownCommands() {
        return knownCommands;
    }

    public Server getServer() {
        return server;
    }

}
