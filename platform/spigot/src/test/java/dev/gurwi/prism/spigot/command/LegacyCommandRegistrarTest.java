package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.command.BukkitExecutableCommand;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LegacyCommandRegistrarTest extends BukkitTest {

    @Test
    void testCommandMapLookup() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand(
                                "prism",
                                "Prism command",
                                "pr"
                        ).build()
                );

        Command command = server.getCommandMap().getCommand("prism");
        Command alias = server.getCommandMap().getCommand("pr");

        assertNotNull(command);
        assertNotNull(alias);

        assertSame(command, alias);
        assertInstanceOf(BukkitExecutableCommand.class, command);
    }

    @Test
    void testCommandMetadata() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand(
                                "prism",
                                List.of("pr"),
                                "Prism command"
                        ).build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);

        assertEquals("prism", command.getName());
        assertEquals("Prism command", command.getDescription());
        assertEquals("prism", command.getLabel());

        assertEquals(1, command.getAliases().size());
        assertEquals("pr", command.getAliases().getFirst());
    }

    @Test
    void testCommandMapRegistration() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new PrismBukkitCommand(
                                "prism",
                                "Prism command",
                                "pr"
                        ).build()
                );

        Command command = server.getCommandMap().getCommand("prism");

        assertNotNull(command);

        assertSame(
                command,
                server.getCommandMap().getCommand("prism")
        );

        assertSame(
                command,
                server.getCommandMap().getCommand("pr")
        );
    }

    @Test
    void testRegisteredCommands() {
        var registry = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry();

        var definition = new PrismBukkitCommand(
                "prism",
                "Prism command",
                "pr"
        ).build();

        registry.register(definition);

        assertTrue(
                registry.getRegisteredCommands()
                        .contains(definition)
        );
    }

    @Test
    void testUnregistration() {
        var registry = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry();

        var definition = new PrismBukkitCommand(
                "prism",
                "Prism command",
                "pr"
        ).build();

        registry.register(definition);

        assertNotNull(
                server.getCommandMap()
                        .getCommand("prism")
        );

        assertNotNull(
                server.getCommandMap()
                        .getCommand("pr")
        );

        registry.unregister(definition);

        assertNull(
                server.getCommandMap()
                        .getCommand("prism")
        );

        assertNull(
                server.getCommandMap()
                        .getCommand("pr")
        );

        assertFalse(
                registry.getRegisteredCommands()
                        .contains(definition)
        );
    }
}