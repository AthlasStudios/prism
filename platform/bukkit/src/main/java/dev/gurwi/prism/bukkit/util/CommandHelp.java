package dev.gurwi.prism.bukkit.util;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommandHelp {

    private static final ChatColor DEFAULT_PRIMARY_COLOR = parseColor("#00E5FF", ChatColor.AQUA);
    private static final ChatColor DEFAULT_SECONDARY_COLOR = parseColor("#01737d", ChatColor.DARK_AQUA);

    public static void help(CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ?> args) {
        help(sender, args, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR);
    }

    public static void help(CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ?> args, ChatColor primaryColor, ChatColor secondaryColor) {
        BukkitCommandDefinition definition = args.getDefinition();

        List<? extends CommandArguments<BukkitCommandDefinition, ?>> chain = args.getChain();
        List<String> pathSegments = new ArrayList<>();

        for (CommandArguments<BukkitCommandDefinition, ?> node : chain) {
            BukkitCommandDefinition nodeDef = node.getDefinition();
            pathSegments.add(nodeDef.getName());

            for (var arg : nodeDef.getRequiredArguments()) {
                if (arg instanceof LiteralArgument) {
                    pathSegments.add(arg.getName());
                    continue;
                }

                String fallback = "<" + arg.getName() + ">";
                pathSegments.add(node.find(arg.getName()).map(val -> display(val, fallback)).orElse(fallback));
            }

            for (var arg : nodeDef.getOptionalArguments()) {
                if (arg instanceof LiteralArgument) {
                    pathSegments.add("[" + arg.getName() + "]");
                    continue;
                }

                String fallback = "[" + arg.getName() + "]";
                pathSegments.add(node.find(arg.getName()).map(val -> display(val, fallback)).orElse(fallback));
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

        List<Entry> entries = new ArrayList<>();

        List<BukkitCommandDefinition> children = new ArrayList<>(args.getChildDefinitions());
        children.sort(Comparator.comparing(BukkitCommandDefinition::getName));

        for (BukkitCommandDefinition child : children) {
            if (!reachable(child, sender)) {
                continue;
            }

            String childCommand = currentFullCommand + " " + child.getName() + arguments(child);

            List<String> childBranches = new ArrayList<>();
            collectBranches(child.getArgumentTreeBranches(), childCommand, sender, childBranches);

            if (child.hasExecutor(sender) || childBranches.isEmpty()) {
                entries.add(new Entry(childCommand, child.getDescription()));
            }

            for (String usage : childBranches) {
                entries.add(new Entry(usage, null));
            }
        }

        List<String> branches = new ArrayList<>();
        collectBranches(definition.getArgumentTreeBranches(), currentFullCommand, sender, branches);

        for (String usage : branches) {
            entries.add(new Entry(usage, null));
        }

        if (entries.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + " No subcommands available.");
        } else {
            for (Entry entry : entries) {
                send(sender, entry, primaryColor, secondaryColor);
            }
        }

        sender.sendMessage("");
    }

    private static @NotNull String display(@Nullable Object value, @NotNull String fallback) {
        if (value instanceof CharSequence
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>) {
            return String.valueOf(value);
        }

        return fallback;
    }

    private static boolean accessible(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        return definition.getRequirement().test(sender)
                && definition.getPermissions().stream().allMatch(permission -> permission.hasPermission(sender));
    }

    private static boolean reachable(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        if (!accessible(definition, sender)) {
            return false;
        }

        if (definition.hasExecutor(sender)) {
            return true;
        }

        List<String> branches = new ArrayList<>();
        collectBranches(definition.getArgumentTreeBranches(), "", sender, branches);

        return !branches.isEmpty() || definition.getChildren().stream().anyMatch(child -> reachable(child, sender));
    }

    private record Entry(@NotNull String command, @Nullable String description) {
    }

    private static void send(@NotNull CommandSender sender, @NotNull Entry entry, @NotNull ChatColor primaryColor, @NotNull ChatColor secondaryColor) {
        String fullCommand = entry.command();

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

        String entryDescription = entry.description();
        if (entryDescription != null && !entryDescription.isBlank()) {
            TextComponent separator = new TextComponent(" — ");
            separator.setColor(ChatColor.DARK_GRAY);
            message.addExtra(separator);

            TextComponent descComponent = new TextComponent(entryDescription);
            descComponent.setColor(ChatColor.GRAY);
            message.addExtra(descComponent);
        }

        sender.spigot().sendMessage(message);
    }

    private static @NotNull String arguments(@NotNull BukkitCommandDefinition definition) {
        StringBuilder builder = new StringBuilder();

        for (BukkitArgument<?, ?> arg : definition.getRequiredArguments()) {
            builder.append(' ').append(token(arg));
        }

        for (BukkitArgument<?, ?> arg : definition.getOptionalArguments()) {
            builder.append(" [").append(arg.getName()).append(']');
        }

        return builder.toString();
    }

    private static void collectBranches(@NotNull List<? extends AbstractTreeArgument<?, ?, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, ?>> nodes, @NotNull String prefix, @NotNull CommandSender sender, @NotNull List<String> usages) {
        for (var node : nodes) {
            if (!node.getRequirement().test(sender)) {
                continue;
            }

            String path = prefix + " " + token(node);

            if (node.getExecutors().stream().anyMatch(executor -> executor.getSourceType().isInstance(sender))) {
                usages.add(path);
            }

            collectBranches(node.getArguments(), path, sender, usages);
        }
    }

    private static @NotNull String token(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof LiteralArgument ? argument.getName() : "<" + argument.getName() + ">";
    }

    private static ChatColor parseColor(String hex, ChatColor fallback) {
        try {
            return ReflectionUtil.callMethod(ChatColor.class, "of", hex);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

}