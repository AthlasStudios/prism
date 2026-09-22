package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.bukkit.permissions.ServerOperator;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class CommandTabCompletionTest extends BukkitTest {

    @Test
    void testCommandIsTabCompletedForAllowedPlayer() {
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
                                .withPlayerRequirement(ServerOperator::isOp)
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        player.setOp(true);

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedForDeniedPlayer() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new SpigotCommand("prism")
                                .withPlayerRequirement(playerMock -> false)
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testPlayerRequirementDoesNotAffectConsole() {
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
                                .withPlayerRequirement(player -> false)
                                .executesConsole((sender, args) -> {})
                                .build()
                );
        
        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(command.testPermissionSilent(server.getConsoleSender()));
    }

}