package dev.gurwi.prism.test.bukkit;

import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandUtils;
import org.bukkit.command.SimpleCommandMap;

import java.util.List;

public class SimpleCommandMapMock extends SimpleCommandMap {

    private final PrismMockServer server;

    public SimpleCommandMapMock(PrismMockServer server) {
        super(server);
        this.server = server;
    }

    @Override
    public boolean register(String label, String fallbackPrefix, Command command) {
        try {
            List<PluginCommand> commands = ReflectionUtil.accessField(server.getPluginManager(), "commands");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return super.register(label, fallbackPrefix, command);
    }

}
