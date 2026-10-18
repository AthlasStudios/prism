package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandPermissionTest extends BukkitTest {

    @Test
    void testCommandIsPermittedWithoutPermissions() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Command command = server.getCommandMap().getCommand("prism");
        Player player = server.addPlayer();

        assertNotNull(command);
        assertTrue(command.testPermission(player));
        assertTrue(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsDeniedWithoutPermission() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .withPermission(BukkitPermission.of("prism.use"))
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Command command = server.getCommandMap().getCommand("prism");
        Player player = server.addPlayer();

        assertNotNull(command);
        assertFalse(player.hasPermission("prism.use"));
        assertFalse(command.testPermission(player));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsPermittedWithPermission() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .withPermission(BukkitPermission.of("prism.use"))
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Player player = server.addPlayer();
        player.addAttachment(plugin, "prism.use", true);

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(player.hasPermission("prism.use"));
        assertTrue(command.testPermission(player));
        assertTrue(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsDeniedWhenRequirementFails() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .withPlayerRequirement(player -> false)
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Command command = server.getCommandMap().getCommand("prism");
        Player player = server.addPlayer();

        assertNotNull(command);
        assertFalse(command.testPermission(player));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsDeniedWhenRequirementFailsEvenWithPermission() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .withPlayerRequirement(player -> false)
                .withPermission(BukkitPermission.of("prism.use"))
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Player player = server.addPlayer();
        player.addAttachment(plugin, "prism.use", true);

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertTrue(player.hasPermission("prism.use"));
        assertFalse(command.testPermission(player));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testCommandIsDeniedWhenPermissionFailsEvenWithRequirement() {
        var platform = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init();

        var definition = new PrismBukkitCommand("prism")
                .withPlayerRequirement(player -> true)
                .withPermission(BukkitPermission.of("prism.use"))
                .executesPlayer((sender, args) -> {})
                .build();

        platform.getCommandsRegistry().register(definition);

        Player player = server.addPlayer();

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);
        assertFalse(player.hasPermission("prism.use"));
        assertFalse(command.testPermission(player));
        assertFalse(command.testPermissionSilent(player));
    }

    @Test
    void testStartsWithPermission() {
        var permission = BukkitPermission.startsWith("prism.command.");

        Player player = server.addPlayer();
        player.addAttachment(
                plugin,
                "prism.command.test",
                true
        );

        assertTrue(permission.hasPermission(player));
    }

    @Test
    void testStartsWithPermissionDoesNotMatch() {
        var permission = BukkitPermission.startsWith("prism.command.");

        Player player = server.addPlayer();
        player.addAttachment(
                plugin,
                "other.command.test",
                true
        );

        assertFalse(permission.hasPermission(player));
    }

    @Test
    void testStartsWithPermissionMatchesPrefixOnly() {
        var permission = BukkitPermission.startsWith("prism.command.");

        Player player = server.addPlayer();
        player.addAttachment(
                plugin,
                "prism.command",
                true
        );

        assertFalse(permission.hasPermission(player));
    }

    @Test
    void testStartsWithPermissionAllowsOperator() {
        var permission = BukkitPermission.startsWith("prism.command.");

        Player player = server.addPlayer();
        player.setOp(true);

        assertTrue(permission.hasPermission(player));
    }

}