package dev.gurwi.prism.bungeecord.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BungeecordCommandShim extends Command implements TabExecutor {

    private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
    private final LiteralCommandNode<CommandSender> node;
    private final String label;
    private final Logger logger;

    public BungeecordCommandShim(@NotNull LiteralCommandNode<CommandSender> node, @NotNull Logger logger) {
        super(node.getName());

        this.node = node;
        this.label = node.getName();
        this.logger = logger;

        this.dispatcher.getRoot().addChild(node);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String input = (label + " " + String.join(" ", args)).trim();

        try {
            dispatcher.execute(input, sender);
        } catch (CommandSyntaxException exception) {
            sender.sendMessage(new TextComponent(ChatColor.RED + exception.getMessage()));
        } catch (Throwable throwable) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "An internal error occurred."));
            logger.log(Level.SEVERE, "Prism failed to execute /" + input, throwable);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String input = label + " " + String.join(" ", args);

        try {
            ParseResults<CommandSender> parse = dispatcher.parse(input, sender);
            Set<String> completions = new LinkedHashSet<>();

            for (Suggestion suggestion : dispatcher.getCompletionSuggestions(parse).get(1, TimeUnit.SECONDS).getList()) {
                completions.add(lastToken(input, suggestion));
            }

            return completions;
        } catch (Throwable throwable) {
            logger.log(Level.WARNING, "Prism failed to complete /" + input, throwable);
            return List.of();
        }
    }

    public LiteralCommandNode<CommandSender> getNode() {
        return node;
    }

    private static @NotNull String lastToken(@NotNull String input, @NotNull Suggestion suggestion) {
        String applied = input.substring(0, suggestion.getRange().getStart()) + suggestion.getText();
        int start = input.lastIndexOf(' ') + 1;

        return start >= applied.length() ? suggestion.getText() : applied.substring(start);
    }

}
