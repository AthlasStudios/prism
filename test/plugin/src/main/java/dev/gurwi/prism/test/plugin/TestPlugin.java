package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.bukkit.argument.GreedyStringArgument;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpigotPlatform platform = SpigotPlatform.configure(this)
                .init();

        new Command("test")
                .withArguments(
                        new StringArgument("str"),
                        new IntegerArgument("int"),
                        new GreedyStringArgument("greedy")
                )
                .executes((sender, args) -> {
                    sender.sendMessage("Command executed!");
                })
                .register(platform);
    }

}
