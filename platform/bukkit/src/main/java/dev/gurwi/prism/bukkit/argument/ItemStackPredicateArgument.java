package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemStackPredicateArgument extends BukkitArgument<ItemStackPredicateArgument, Predicate<ItemStack>> {

    public ItemStackPredicateArgument(String name) {
        super(name);
    }

    @Override
    public Predicate<ItemStack> parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        throw new ArgumentParseException(this, value, "Item predicates require Brigadier");
    }

}