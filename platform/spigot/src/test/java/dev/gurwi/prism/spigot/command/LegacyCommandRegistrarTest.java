package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyCommandRegistrarTest extends BukkitTest {

    @Test
    public void testLegacyCommandRegistrar() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(new Command("prism", "pr").build());

        assertNotNull(server.getCommandMap().getCommand("prism"));
    }

}