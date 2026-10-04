package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler.defaultHandler())
                .init();

        new SpigotCommand("test")
                .withArguments(
                        new StringArgument("color")
                )
                .withSubCommands(
                        new SpigotCommand("call")
                                .executes((sender, args) -> {
                                    sender.sendMessage("Called team " + args.getAt("../", "color"));
                                }),

                        new SpigotCommand("teleport")
                                .withArguments(
                                        new LocationArgument("location")
                                )
                                .executesPlayer((player, args) -> {
                                    Location location = args.get("location");
                                    player.teleport(location);
                                    player.sendMessage("Teleported to " + location);
                                })

                )
                .executes((sender, args) -> {
                    sender.sendMessage("Selected team color " + args.get("color"));
                })
                .register(platform);

    }

}
