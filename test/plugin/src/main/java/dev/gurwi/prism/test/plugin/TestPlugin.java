package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.GreedyStringArgument;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.bukkit.permission.CommandPermission;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .init();

        new SpigotCommand("test")
                .withArguments(
                        new StringArgument("str"),
                        new IntegerArgument("int"),
                        new GreedyStringArgument("greedy")
                )
                .withPermissions(CommandPermission.OP)
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("Command executed!");
                })
                .register(platform);

    }

}
