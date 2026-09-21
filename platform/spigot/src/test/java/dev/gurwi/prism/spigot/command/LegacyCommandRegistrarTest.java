package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.test.bukkit.BukkitTest;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LegacyCommandRegistrarTest extends BukkitTest {

    @Test
    void testPluginCommandLookup() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command(
                                "prism",
                                "Prism command",
                                "pr"
                        ).build()
                );

        PluginCommand command = plugin.getCommand("prism");
        PluginCommand alias = plugin.getCommand("pr");

        assertNotNull(command);
        assertNotNull(alias);

        assertSame(command, alias);
    }

    @Test
    void testCommandMetadata() {
        SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry()
                .register(
                        new Command(
                                "prism",
                                List.of("pr"),
                                "Prism command"
                        ).build()
                );

        PluginCommand command = plugin.getCommand("prism");

        assertNotNull(command);

        assertEquals("prism", command.getName());
        assertEquals("Prism command", command.getDescription());
        assertEquals(plugin, command.getPlugin());
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
                        new Command(
                                "prism",
                                "Prism command",
                                "pr"
                        ).build()
                );

        PluginCommand command = plugin.getCommand("prism");

        assertNotNull(command);

        assertSame(
                command,
                server.getCommandMap().getCommand("prism")
        );

        assertSame(
                command,
                server.getCommandMap().getCommand("pr")
        );

        assertSame(
                command,
                server.getPluginCommand("prism")
        );

        assertSame(
                command,
                server.getPluginCommand("pr")
        );
    }

    @Test
    void testRegisteredCommands() {
        var registry = SpigotPlatform.configure(plugin)
                .disableBrigadier()
                .init()
                .getCommandsRegistry();

        var definition = new Command(
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

        var definition = new Command(
                "prism",
                "Prism command",
                "pr"
        ).build();

        registry.register(definition);

        assertNotNull(plugin.getCommand("prism"));
        assertNotNull(plugin.getCommand("pr"));

        registry.unregister(definition);

        assertNull(plugin.getCommand("prism"));
        assertNull(plugin.getCommand("pr"));

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