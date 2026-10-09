package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMapper;
import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMappings;
import dev.gurwi.prism.bukkit.argument.FloatArgument;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.LocationArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.paper.PaperPlatform;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        ArgumentMappings.INSTANCE.register(LocationArgument.class, ArgumentMapper.create(argument -> {
            return ArgumentTypes.finePosition();
        }, (item, source, value) -> value));
    }

    @Override
    public void onEnable() {
        PaperPlatform platform = PaperPlatform.configure(this)
                .init();

        BukkitCommandDefinition build = new PrismBukkitCommand("testa", Collections.emptyList(), "Test command")
                .withArguments(
                        new StringArgument("color")
                )
                .executes((sender, args) -> {
                    sender.sendMessage("This works");
                })
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
                                                .replaceSuggestions("5", "10")
                                )
                                .withOptionalArguments(
                                        new FloatArgument("float", 0.1f, 9.9f)
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
