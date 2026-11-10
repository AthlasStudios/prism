package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.bukkit.command.PrismCommand;
import dev.gurwi.prism.spigot.SpigotCommandPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutionTest extends BukkitTest {

    // TODO -> WRITE ARGUMENT TESTS
    // TODO -> WRITE SUB COMMAND TESTS

    @Test
    void testCommandIsRegistered() {
        SpigotCommandPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismCommand(
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

        SpigotCommandPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismCommand(
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

        SpigotCommandPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismCommand(
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

//    @Test
//    void testCommandExecutionThrowsWhenNoExecutorIsPresent() {
//        PlayerMock player = server.addPlayer();
//
//        SpigotCommandPlatform.configure(plugin)
//                .disableBrigadier()
//                .init()
//                .getCommandsRegistry()
//                .register(
//                        new PrismCommand(
//                                "prism",
//                                "Prism command",
//                                "pr"
//                        )
//                                .executesConsole((sender, args) -> {})
//                                .build()
//                );
//
//        Command command = server.getCommandMap().getCommand("prism");
//
//        assertNotNull(command);
//        assertInstanceOf(BukkitExecutableCommand.class, command);
//
//        BukkitExecutableCommand executableCommand = (BukkitExecutableCommand) command;
//
//        assertThrows(
//                NoExecutorException.class,
//                () -> executableCommand.execute(
//                        player,
//                        "prism",
//                        new String[0]
//                )
//        );
//    }

}