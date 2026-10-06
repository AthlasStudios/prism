package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.LocationArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.paper.PaperPlatform;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperPlatform platform = PaperPlatform.configure(this)
                .withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler.defaultHandler())
                .init();

        BukkitCommandDefinition build = new PrismBukkitCommand("test")
                .withArguments(
                        new StringArgument("color")
                )
                .withSubCommands(
                        new PrismBukkitCommand("call")
                                .executes((sender, args) -> {
                                    sender.sendMessage("Called team " + args.getAt("../", "color"));
                                }),

                        new PrismBukkitCommand("teleport")
                                .withArguments(
                                        new LocationArgument("location")
                                )
                                .executesPlayer((player, args) -> {
                                    Location location = args.get("location");
                                    player.teleport(location);
                                    player.sendMessage("Teleported to " + location);
                                }),

                        new PrismBukkitCommand("number")
                                .withArguments(
                                        new IntegerArgument("number", 0, 10)
                                )
                                .executes((sender, args) -> {
                                    Integer number = args.get("number");
                                    sender.sendMessage("Number is " + number);
                                })

                )
                .executes((sender, args) -> {
                    sender.sendMessage("Selected team color " + args.get("color"));
                }).build();

        platform.getCommandsRegistry().register(build);
    }

}
