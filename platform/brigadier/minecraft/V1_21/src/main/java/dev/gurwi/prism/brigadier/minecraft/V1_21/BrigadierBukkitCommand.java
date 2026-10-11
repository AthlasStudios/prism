package dev.gurwi.prism.brigadier.minecraft.V1_21;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

final class BrigadierBukkitCommand extends VanillaCommandWrapper {

    private final CommandDispatcher<MinecraftCommandSource> own;
    private final String root;

    BrigadierBukkitCommand(CommandDispatcher<MinecraftCommandSource> own, LiteralCommandNode<CommandSourceStack> node, String description, List<String> aliases) {
        super(node);
        this.own = own;
        this.root = node.getName();

        setDescription(description == null ? "" : description);
        setAliases(aliases);
        setPermission(null);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            own.execute(input(args), source(sender));
        } catch (CommandSyntaxException exception) {
            sender.sendMessage(exception.getMessage());
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        try {
            return own.getCompletionSuggestions(own.parse(input(args), source(sender)))
                    .get()
                    .getList()
                    .stream()
                    .map(Suggestion::getText)
                    .toList();
        } catch (InterruptedException | ExecutionException exception) {
            return List.of();
        }
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabComplete(sender, alias, args);
    }

    private String input(String @NotNull [] args) {
        return args.length == 0 ? root : root + " " + String.join(" ", args);
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull MinecraftCommandSource source(CommandSender sender) {
        return MinecraftCommandSource.create(() -> sender, () -> sender instanceof Entity ? (Entity) sender : null);
    }

}