package dev.gurwi.prism.spigot;

import dev.gurwi.prism.spigot.command.Command;
import dev.gurwi.prism.spigot.requirement.SpigotExecutionRequirement;
import org.bukkit.plugin.java.JavaPlugin;

public class Test extends JavaPlugin {

    @Override
    public void onEnable() {

        SpigotPlatform platform = SpigotPlatform.configure(this)
                .disableBrigadier()
                .init();

        new Command("test", "t")
                .executes((sender, args) -> sender.sendMessage("Hi Sender?"))
                .executesPlayer((player, args) -> player.sendMessage("Hello World!"))
                .executesConsole((console, args) -> console.sendMessage("Hi Console?"))
                .withPermission("test.command")
                .withRequirement(SpigotExecutionRequirement.isOp(), SpigotExecutionRequirement.permission("test.command"))
                .withPlayerRequirement(player -> player.teleport(player.getLocation().add(0, 1, 0)))
                .withSubCommand()
                .register(platform);
    }

}
