package dev.gurwi.prism.brigadier.minecraft.bukkit;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public final class BrigadierBukkitCommand<SourceStack> extends Command {

    private final CommandDispatcher<MinecraftCommandSource> own;
    private final String root;

    public BrigadierBukkitCommand(CommandDispatcher<MinecraftCommandSource> own, @NotNull LiteralCommandNode<SourceStack> node, String description, List<String> aliases) {
        super(node.getName(), description == null ? "" : description, "/" + node.getName(), aliases);
        this.own = own;
        this.root = node.getName();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        try {
            run(own.parse(input(args), source(sender)));
        } catch (CommandSyntaxException exception) {
            sender.sendMessage(describe(exception));
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) {
        try {
            return own.getCompletionSuggestions(own.parse(input(args), source(sender)))
                    .get()
                    .getList()
                    .stream()
                    .map(Suggestion::getText)
                    .toList(); // TODO -> WTF
        } catch (InterruptedException | ExecutionException exception) {
            return List.of();
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args, Location location) {
        return tabComplete(sender, alias, args);
    }

    private void run(@NotNull ParseResults<MinecraftCommandSource> parsed) throws CommandSyntaxException {
        if (parsed.getReader().canRead()) {
            CommandSyntaxException deepest = null;

            for (CommandSyntaxException exception : parsed.getExceptions().values()) {
                if (deepest == null || exception.getCursor() > deepest.getCursor()) {
                    deepest = exception;
                }
            }

            if (deepest != null) {
                throw deepest;
            }
        }

        own.execute(parsed);
    }

    private @NotNull String describe(@NotNull CommandSyntaxException exception) {
        String message = ChatColor.RED + exception.getRawMessage().getString();
        String context = exception.getContext();

        if (context == null || exception.getCursor() < 0) {
            return message;
        }

        return message + ChatColor.GRAY + " at position " + exception.getCursor() + ": " + context;
    }

    private String input(String @NotNull [] args) {
        return args.length == 0 ? root : root + " " + String.join(" ", args);
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull MinecraftCommandSource source(CommandSender sender) {
        return MinecraftCommandSource.create(() -> sender, () -> sender instanceof Entity ? (Entity) sender : null);
    }

}