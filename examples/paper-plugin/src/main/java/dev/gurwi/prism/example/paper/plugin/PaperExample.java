package dev.gurwi.prism.example.paper.plugin;

import dev.gurwi.prism.bukkit.BukkitPlatform;
import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.argument.base.EntitySelectorArgument;
import dev.gurwi.prism.bukkit.argument.parser.ParsedFlags;
import dev.gurwi.prism.bukkit.argument.suggestion.ArgumentSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.argument.type.Flag;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.bukkit.util.CommandHelp;
import dev.gurwi.prism.paper.PaperPlatform;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class PaperExample extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperPlatform platform = PaperPlatform.configure(this)
                .init();

        BukkitCommandDefinition build = new PrismBukkitCommand("testa", "pr")
                .withDescription("Prism command tester")
                .withArguments(
                        new PlayerArgument("player"),
                        new OfflinePlayerArgument("offlinePlayer"),
                        new EntitySelectorArgument.OnePlayer("idk"),
                        new ColorArgument("color")
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
                                }),

                        new PrismBukkitCommand("hex")
                                .withArguments(new HexColorArgument("hex"))
                                .executes((sender, args) -> {
                                    Color color = args.get("hex");

                                    net.kyori.adventure.text.Component message = net.kyori.adventure.text.Component.text("This message is custom colored!")
                                            .color(net.kyori.adventure.text.format.TextColor.color(color.asRGB()));

                                    sender.sendMessage(message);
                                }),

                        new PrismBukkitCommand("material")
                                .withArguments(new MaterialArgument("material"))
                                .executes((sender, args) -> {
                                    Material material = args.get("material");
                                    sender.sendMessage("Selected material " + material);
                                }),

                        new PrismBukkitCommand("particle")
                                .withArguments(new ParticleArgument("particle"))
                                .executes((sender, args) -> {
                                    Particle particle = args.get("particle");
                                    sender.sendMessage("Selected particle " + particle);
                                }),

                        new PrismBukkitCommand("difficulty")
                                .withArguments(new DifficultyArgument("difficulty"))
                                .executes((sender, args) -> {
                                    Difficulty difficulty = args.get("difficulty");
                                    sender.sendMessage("Selected difficulty " + difficulty.name());
                                }),

                        new PrismBukkitCommand("idk")
                                .withArguments(
                                        new StringArgument("string")
                                                .replaceSuggestions(
                                                        BukkitSuggestionsSupplier.sync(
                                                                ctx -> List.of(ArgumentSuggestion.of("test", "test tooltip"))
                                                        )
                                                )
                                )
                                .executes((sender, args) -> {
                                    sender.sendMessage("Selected string " + args.get("string"));
                                })
                )
                .executes((sender, args) -> {
                    ChatColor color = args.get("color");
                    sender.sendMessage("Selected team color " + color.name());
                })
                .build();

        StringArgument string = new StringArgument("string");

        new PrismBukkitCommand("prism", "pri")
                .withArguments(
                        new ItemStackPredicateArgument("predicate")
                )
                .then(
                        new MaterialArgument("material")
                                .executes((sender, args) -> {
                                    Material material = args.get("material");
                                    sender.sendMessage("Selected material " + material);
                                })
                )
                .then(
                        new GameModeArgument("gamemode")
                                .executes((sender, args) -> {
                                    GameMode gameMode = args.get("gamemode");
                                    sender.sendMessage("Selected game mode " + gameMode);
                                })
                )
                .then(
                        new DifficultyArgument("difficulty")
                                .executes((sender, args) -> {
                                    Difficulty difficulty = args.get("difficulty");
                                    sender.sendMessage("Selected difficulty " + difficulty);
                                })
                )
                .executesPlayer((sender, args) -> {
                    Predicate<ItemStack> predicate = args.get("predicate");
                    sender.sendMessage("Help command executed " + predicate.test(sender.getInventory().getItemInMainHand()));

                    CommandHelp.help(sender, args);
                })
                .register(platform);

        BukkitCommandDefinition definition = new PrismBukkitCommand("flagger")
                .withArguments(
                        new FlagArgument("flags")
                                .flags(
                                        Flag.valued(new ColorArgument("color"), 'c'),
                                        Flag.presence("enable", 'e')
                                )
                )
                .executesPlayer((player, args) -> {
                    ParsedFlags flags = args.get("flags");
                    Optional<ChatColor> color = flags.get("color");

                    color.ifPresent(c -> player.sendMessage("Color: " + c.name()));
                })
                .register(platform);

        platform.getCommandsRegistry().register(build);
        register(platform, this);
//        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
//            platform.getCommandsRegistry().unregister(build);
//            getLogger().info("Unregistered build command");
//        }, 20 * 5);
//
//        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
//            platform.getCommandsRegistry().register(build);
//            getLogger().info("Registered build command");
//        }, 20 * 10); TODO -> RECHECK I THINK AN ERROR OCCURED
    }

    public static BukkitCommandDefinition register(BukkitPlatform<?, ?> platform, Plugin plugin) {
        BukkitSuggestionsSupplier.Async slow = context -> CompletableFuture.supplyAsync(() -> {
            plugin.getLogger().info("async supplier on " + Thread.currentThread().getName() + " input=" + context.getCurrentRawArg());

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return List.of(
                    ArgumentSuggestion.of("alpha", "first async value"),
                    ArgumentSuggestion.of("alphabet", "shares a prefix with alpha"),
                    ArgumentSuggestion.of("beta", "second async value"),
                    ArgumentSuggestion.of("gamma", "third async value")
            );
        });

        BukkitSuggestionsSupplier.Sync instant = context -> {
            plugin.getLogger().info("sync supplier on " + Thread.currentThread().getName());

            return List.of(
                    ArgumentSuggestion.of("one", "sync value"),
                    ArgumentSuggestion.of("two", "sync value")
            );
        };

        return new PrismBukkitCommand("asynctest")
                .withArguments(
                        new StringArgument("slow").replaceSuggestions(slow),
                        new StringArgument("fast").replaceSuggestions(instant)
                )
                .executes((sender, args) -> {
                    sender.sendMessage("slow: " + args.get("slow"));
                    sender.sendMessage("fast: " + args.get("fast"));
                })
                .register(platform);
    }

}
