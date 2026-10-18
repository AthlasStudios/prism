package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.permission.CommandPermission;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class CommandTabCompletionTest extends BukkitTest {

    @Test
    void testCommandIsTabCompletedForAllowedPlayer() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand(
                                "prism",
                                "Prism command",
                                "pr"
                        )
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedForNonOpPlayer() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedForDeniedPlayer() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .withPlayerRequirement(playerMock -> false)
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedForPlayerWithoutExecutor() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .executesConsole((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedWithoutPermission() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .withPermission(
                                        BukkitPermission.of("prism.use")
                                )
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(player.hasPermission("prism.use"));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsTabCompletedWithPermission() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .withPermission(
                                        BukkitPermission.of("prism.use")
                                )
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        player.addAttachment(plugin, "prism.use", true);

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(player.hasPermission("prism.use"));
        assertTrue(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsNotTabCompletedWhenRequirementFailsEvenWithPermission() {
        PlayerMock player = server.addPlayer();

        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .withPlayerRequirement(playerMock -> false)
                                .withPermission(
                                        BukkitPermission.of("prism.use")
                                )
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        player.addAttachment(plugin, "prism.use", true);

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(player.hasPermission("prism.use"));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testPlayerRequirementDoesNotAffectConsole() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand(
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

    @Test
    void testConsoleIsNotTabCompletedForPlayerOnlyCommand() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand("prism")
                                .executesPlayer((sender, args) -> {})
                                .build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(command.testPermissionSilent(server.getConsoleSender()));
    }

    @Test
    void testCommandPermissionOp() {
        PlayerMock player = server.addPlayer();

        assertFalse(CommandPermission.OP.hasPermission(player));

        player.setOp(true);

        assertTrue(CommandPermission.OP.hasPermission(player));
    }

    @Test
    void testCommandPermissionNone() {
        PlayerMock player = server.addPlayer();

        assertFalse(CommandPermission.NONE.hasPermission(player));
    }

}