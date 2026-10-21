package club.athlas.prism.command;

import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class PrismInfoCommand extends PrismBukkitCommand {

    public PrismInfoCommand() {
        super("prism");

        executes((sender, args) -> {
            TextComponent message = new TextComponent(
                    ChatColor.AQUA + "" + ChatColor.BOLD + "PRISM"
                            + ChatColor.DARK_GRAY + " » "
                            + ChatColor.GRAY + "A code-first command framework that lets you build commands directly in Java with a fluent, type-safe API. No annotations required."
                            + ChatColor.DARK_GRAY + " • "
            );

            TextComponent link = new TextComponent(
                    ChatColor.AQUA + "" + ChatColor.UNDERLINE + "GitHub"
            );

            link.setClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    "https://github.com/AthlasStudios/prism"
            ));

            link.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.GRAY + "Click to view Prism on GitHub").create()
            ));

            message.addExtra(link);

            sender.spigot().sendMessage(message);
        });
    }

}