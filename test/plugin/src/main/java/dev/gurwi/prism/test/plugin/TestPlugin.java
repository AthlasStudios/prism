package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler.defaultHandler())
                .init();

        new SpigotCommand("test")
                .withArguments(
                        new EntitySelectorArgument.ManyEntities("selector")
                )
                .withOptionalArguments(
                        new EnchantmentArgument("enchant")
                )
                .executes((sender, args) -> {

                })
                .register(platform);

    }

}
