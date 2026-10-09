package dev.gurwi.prism.bukkit.wrapper;

import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WrappedCommandMap implements CommandMap {

    private final Server server;
    private final CommandMap commandMap;
    private final Map<String, Command> knownCommands;

    public WrappedCommandMap(Server server) throws NoSuchFieldException, IllegalAccessException {
        this.server = server;
        this.commandMap = ReflectionUtil.accessField(
                server,
                "commandMap"
        );

        this.knownCommands = ReflectionUtil.accessFieldFromType(
                SimpleCommandMap.class,
                commandMap,
                "knownCommands"
        );
    }

    @Override
    public void registerAll(@NotNull String fallbackPrefix, @NotNull List<Command> commands) {
        commandMap.registerAll(fallbackPrefix, commands);
    }

    @Override
    public boolean register(@NotNull String label, @NotNull String fallbackPrefix, @NotNull Command command) {
        return commandMap.register(label, fallbackPrefix, command);
    }

    @Override
    public boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        return commandMap.register(fallbackPrefix, command);
    }

    @Override
    public boolean dispatch(@NotNull CommandSender sender, @NotNull String cmdLine) throws CommandException {
        return commandMap.dispatch(sender, cmdLine);
    }

    @Override
    public void clearCommands() {
        commandMap.clearCommands();
    }

    @Override
    public @Nullable Command getCommand(@NotNull String name) {
        return commandMap.getCommand(name);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine) throws IllegalArgumentException {
        return commandMap.tabComplete(sender, cmdLine);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdLine, @Nullable Location location) throws IllegalArgumentException {
        return commandMap.tabComplete(sender, cmdLine, location);
    }

    public Map<String, Command> getKnownCommands() {
        return knownCommands;
    }

    public Server getServer() {
        return server;
    }

}
