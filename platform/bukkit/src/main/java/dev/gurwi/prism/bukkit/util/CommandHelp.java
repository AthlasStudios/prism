package dev.gurwi.prism.bukkit.util;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHelp {

    private static final ChatColor DEFAULT_PRIMARY_COLOR = parseColor("#00E5FF", ChatColor.AQUA);
    private static final ChatColor DEFAULT_SECONDARY_COLOR = parseColor("#01737d", ChatColor.DARK_AQUA);

    public static void help(CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ? super CommandSender> args) {
        help(sender, args, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR);
    }

    public static void help(CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ? super CommandSender> args, ChatColor primaryColor, ChatColor secondaryColor) {
        BukkitCommandDefinition definition = args.getDefinition();

        List<? extends CommandArguments<BukkitCommandDefinition, ? super CommandSender>> chain = args.getChain();
        List<String> pathSegments = new ArrayList<>();

        for (CommandArguments<BukkitCommandDefinition, ? super CommandSender> node : chain) {
            BukkitCommandDefinition nodeDef = node.getDefinition();
            pathSegments.add(nodeDef.getName());

            for (var arg : nodeDef.getRequiredArguments()) {
                node.find(arg.getName())
                        .ifPresentOrElse(
                                val -> pathSegments.add(String.valueOf(val)),
                                () -> pathSegments.add("<" + arg.getName() + ">")
                        );
            }

            for (var arg : nodeDef.getOptionalArguments()) {
                node.find(arg.getName())
                        .ifPresentOrElse(
                                val -> pathSegments.add(String.valueOf(val)),
                                () -> pathSegments.add("[" + arg.getName() + "]")
                        );
            }
        }

        String commandPath = String.join(" ", pathSegments);
        String currentFullCommand = "/" + commandPath;

        sender.sendMessage("");

        TextComponent currentMessage = new TextComponent(" ");

        TextComponent arrowComponent = new TextComponent("» ");
        arrowComponent.setColor(ChatColor.DARK_GRAY);
        arrowComponent.setBold(true);
        currentMessage.addExtra(arrowComponent);

        TextComponent currentCmdComponent = new TextComponent(currentFullCommand);
        currentCmdComponent.setColor(secondaryColor);
        currentMessage.addExtra(currentCmdComponent);

        String description = definition.getDescription();
        if (description != null && !description.isBlank()) {
            TextComponent descComponent = new TextComponent(" (" + description + ")");
            descComponent.setColor(ChatColor.GRAY);
            currentMessage.addExtra(descComponent);
        }

        sender.spigot().sendMessage(currentMessage);
        sender.sendMessage("");

        List<BukkitCommandDefinition> children = args.getChildDefinitions().stream()
                .filter(child -> child.canExecute(sender))
                .toList();

        if (!children.isEmpty()) {
            for (BukkitCommandDefinition child : children) {
                StringBuilder childCommandBuilder = new StringBuilder(currentFullCommand).append(" ").append(child.getName());

                for (var arg : child.getRequiredArguments()) {
                    childCommandBuilder.append(" <").append(arg.getName()).append(">");
                }
                for (var arg : child.getOptionalArguments()) {
                    childCommandBuilder.append(" [").append(arg.getName()).append("]");
                }

                String fullCommand = childCommandBuilder.toString();

                TextComponent message = new TextComponent(" ");

                TextComponent bullet = new TextComponent("▪ ");
                bullet.setColor(secondaryColor);
                message.addExtra(bullet);

                TextComponent commandComponent = new TextComponent(fullCommand);
                commandComponent.setColor(primaryColor);
                commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, fullCommand));

                TextComponent hoverText = new TextComponent("Click to suggest ");
                hoverText.setColor(ChatColor.GRAY);
                TextComponent hoverCmd = new TextComponent(fullCommand);
                hoverCmd.setColor(primaryColor);
                hoverText.addExtra(hoverCmd);

                commandComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { hoverText }));
                message.addExtra(commandComponent);

                String childDesc = child.getDescription();
                if (childDesc != null && !childDesc.isBlank()) {
                    TextComponent separator = new TextComponent(" — ");
                    separator.setColor(ChatColor.DARK_GRAY);
                    message.addExtra(separator);

                    TextComponent descComponent = new TextComponent(childDesc);
                    descComponent.setColor(ChatColor.GRAY);
                    message.addExtra(descComponent);
                }

                sender.spigot().sendMessage(message);
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + " No subcommands available.");
        }

        sender.sendMessage("");
    }

    private static ChatColor parseColor(String hex, ChatColor fallback) {
        try {
            return ReflectionUtil.callMethod(ChatColor.class, "of", hex);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

}