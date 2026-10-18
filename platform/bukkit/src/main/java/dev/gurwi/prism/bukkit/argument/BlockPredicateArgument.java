package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.type.BlockInWorldPredicate;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BlockPredicateArgument extends BukkitArgument<BlockPredicateArgument, BlockInWorldPredicate> {

    public BlockPredicateArgument(String name) {
        super(name);
    }

    @Override
    public BlockInWorldPredicate parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        throw new ArgumentParseException(this, value, "Block predicates require Brigadier");
    }

}