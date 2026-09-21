package dev.gurwi.prism.test.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class PrismMockServer extends ServerMock {

    private final CommandMap commandMap = new SimpleCommandMap(this);

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public static @NonNull PrismMockServer mock() {
        try {
            Field mockField = MockBukkit.class.getDeclaredField("mock");
            mockField.setAccessible(true);

            if (mockField.get(null) != null) {
                throw new IllegalStateException("Already mocking");
            }

            PrismMockServer mock = new PrismMockServer();

            mockField.set(null, mock);

            Level defaultLevel = mock.getLogger().getLevel();
            mock.getLogger().setLevel(Level.WARNING);

            Bukkit.setServer(mock);

            mock.getLogger().setLevel(defaultLevel);

            return mock;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
