package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EnchantmentArgument extends BukkitArgument<EnchantmentArgument, Enchantment> {

    public EnchantmentArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(Enchantment.values())
                                .map(enchantment -> enchantment.getName().toLowerCase())
                                .toList()
                )
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public Enchantment parse(@NotNull String value) throws ArgumentParseException {
        Enchantment enchantment = Enchantment.getByName(value.toUpperCase());

        if (enchantment == null) {
            throw new ArgumentParseException(this, value, "Enchantment not found: " + value);
        }

        return enchantment;
    }

}
