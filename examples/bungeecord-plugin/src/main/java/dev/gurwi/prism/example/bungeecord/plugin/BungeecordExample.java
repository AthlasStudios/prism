package dev.gurwi.prism.example.bungeecord.plugin;

import dev.gurwi.prism.bungeecord.BungeecordPlatform;
import dev.gurwi.prism.bungeecord.argument.IntegerArgument;
import dev.gurwi.prism.bungeecord.command.BungeecordCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeecordExample extends Plugin {

    @Override
    public void onEnable() {
        BungeecordPlatform platform = BungeecordPlatform.configure(this)
                .init();

        new BungeecordCommand("prism")
                .withOptionalArguments(new IntegerArgument("n", 0, 10))
                .executes((sender, args) -> sender.sendMessage("Hello World!"))
                .register(platform);
    }

}
