package dev.gurwi.prism.test.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.gurwi.prism.Prism;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BukkitTest {

    protected PrismMockServer server;
    protected JavaPlugin plugin;

    @BeforeEach
    public void setup() {
        server = PrismMockServer.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unload();

        server = null;
        plugin = null;
    }

}
