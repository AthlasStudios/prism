package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCommandRegistrar;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftCommandDispatcherProvider;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandDispatcher;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.LocationArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.paper.PaperPlatform;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class TestPlugin extends JavaPlugin {

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
//        MinecraftCommandDispatcherProvider dispatcher = MinecraftCommandDispatcherProvider.get(this).orElseThrow();
//        new BrigadierCommandRegistrar(dispatcher).register(build);

//        MinecraftCommandDispatcherProvider provider = MinecraftCommandDispatcherProvider.get(this).orElseThrow();
//        BrigadierCommandRegistrar brigadierCommandRegistrar = new BrigadierCommandRegistrar(provider.getDispatcher());
//
//        brigadierCommandRegistrar.register(build);
//
//        brigadierCommandRegistrar.register(
//                new PrismBukkitCommand("pri")
//                        .withOptionalArguments(
//                                new StringArgument("str")
//                                        .replaceSuggestions("Stringa", "Cocker", "Pocker")
//                        )
//                        .executes((sender, args) -> sender.sendMessage("Hello World"))
//                        .build()
//        );
    }

}
