package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.argument.ArgumentGroup;
import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.bukkit.permission.CommandPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitUsageRequirement;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler.defaultHandler())
                .init();

        new SpigotCommand("test")
                .withArguments(
                            new StringArgument("str"),
                            new BooleanArgument("bool"),
                            new BiomeArgument("biome"),
                            new GameModeArgument("gamemode")
                )
                .withOptionalArguments(
                        new EnchantmentArgument("enchant")
                )
                .executes((sender, args) -> {
                    String str = args.get("str");
                    boolean boolArg = args.get("bool");
                    Biome biomeArg = args.get("biome");
                    GameMode gameModeArg = args.get("gamemode");
                    Optional<Enchantment> enchantmentArg = args.getOptional("enchant");

                    sender.sendMessage(str);
                    sender.sendMessage(String.valueOf(boolArg));
                    sender.sendMessage(biomeArg.toString());
                    sender.sendMessage(gameModeArg.toString());
                })
                .register(platform);

    }

}
