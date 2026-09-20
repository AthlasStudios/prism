package dev.gurwi.prism.test.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BukkitTest {

    protected ServerMock server;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unload();
    }

}
