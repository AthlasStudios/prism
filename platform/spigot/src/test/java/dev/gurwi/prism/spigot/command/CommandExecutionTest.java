package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutionTest extends BukkitTest {

    @Test
    void testCommandIsRegistered() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new SpigotCommand(
                                "prism",
                                "Prism command",
                                "pr"
                        )
                                .executes((sender, args) -> {
                                })
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertInstanceOf(BukkitExecutableCommand.class, command);

        BukkitExecutableCommand executableCommand = (BukkitExecutableCommand) command;

        assertNotNull(executableCommand.getDefinition());
        assertFalse(executableCommand.getDefinition().getExecutors().isEmpty());
    }

    @Test
    void testCommandExecution() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new SpigotCommand(
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
                        new SpigotCommand(
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