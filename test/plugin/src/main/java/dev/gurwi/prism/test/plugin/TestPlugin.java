package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.paper.PaperPlatform;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
//        ArgumentMappings.INSTANCE.register(
//                LocationArgument.class,
//                ArgumentMapper.<Location, FinePositionResolver, LocationArgument>create(
//                        argument -> ArgumentTypes.finePosition(),
//                        (argument, source, resolver) -> {
//                            var stack = ((WrappedSourceStack) source).getHandle();
//
//                            return resolver.resolve(stack).toLocation(stack.getLocation().getWorld());
//                        }
//                )
//        );
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
                                }),

                        new PrismBukkitCommand("gamemode")
                                .withArguments(
                                        new GameModeArgument("gamemode")
                                )
                                .executes((sender, args) -> {
                                    GameMode gameMode = args.get("gamemode");
                                    sender.sendMessage("Selected game mode " + gameMode);
                                }),

                        new PrismBukkitCommand("select")
                                .withArguments(
                                        new EntitySelectorArgument.ManyEntities("entities")
                                )
                                .executes((sender, args) -> {
                                    List<Entity> entities = args.get("entities");
                                    sender.sendMessage("Selected entities " + entities);
                                }),

                        new PrismBukkitCommand("range")
                                .withArguments(
                                        new IntegerRangeArgument("range")
                                )
                                .executes((sender, args) -> {
                                    RangedNumber<Integer> range = args.get("range");
                                    sender.sendMessage("Selected range " + range.getLowerBound() + " " + range.getUpperBound());
                                })

                )
                .executes((sender, args) -> {
                    sender.sendMessage("Selected team color " + args.get("color"));
                }).build();

        platform.getCommandsRegistry().register(build);
    }

}
