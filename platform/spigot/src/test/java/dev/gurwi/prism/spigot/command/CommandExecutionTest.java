package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutionTest extends BukkitTest {

    @Test
    void testCommandExecutorIsRegistered() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command(
                                "prism",
                                "Prism command",
                                "pr"
                        )
                                .executes((sender, args) -> {
                                })
                                .build()
                );

        PluginCommand command = plugin.getCommand("prism");

        assertNotNull(command);
        assertNotNull(command.getExecutor());
    }

    @Test
    void testCommandExecution() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command(
                                "prism",
                                "Prism command",
                                "pr"
                        )
                                .executes((sender, args) -> sender.sendMessage("executed"))
                                .build()
                );

        assertTrue(
                server.dispatchCommand(
                        player,
                        "prism"
                )
        );

        assertEquals(
                "executed",
                player.nextMessage()
        );
    }

    @Test
    void testAliasExecution() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command(
                                "prism",
                                "Prism command",
                                "pr"
                        )
                                .executes((sender, args) -> sender.sendMessage("executed"))
                                .build()
                );

        assertTrue(
                server.dispatchCommand(
                        player,
                        "pr"
                )
        );

        assertEquals(
                "executed",
                player.nextMessage()
        );
    }
}