package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EnchantmentArgument extends BukkitArgument<EnchantmentArgument, Enchantment> {

    public EnchantmentArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Enchantment.values())
                .map(enchantment -> enchantment.getName().toLowerCase())
                .toList();

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Enchantment parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Enchantment enchantment = Enchantment.getByName(value.toUpperCase());

        if (enchantment == null) {
            throw new ArgumentParseException(this, value, "Enchantment not found: " + value);
        }

        return enchantment;
    }

}
