package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutionTest extends BukkitTest {

    @Test
    void testLegacyCommandExecution() {
        AtomicBoolean executed = new AtomicBoolean(false);

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command("prism", "pr")
                                .executesPlayer((player, args) -> {
                                    executed.set(true);
                                })
                                .build()
                );

        assertNotNull(server.getCommandMap().getCommand("prism"));
        assertTrue(server.dispatchCommand(server.addPlayer("test"), "prism"));
        assertTrue(executed.get());
    }

}
