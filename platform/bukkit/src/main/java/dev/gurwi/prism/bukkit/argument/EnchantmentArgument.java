package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantmentArgument extends BukkitArgument<EnchantmentArgument, Enchantment> {

    public EnchantmentArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Enchantment.values())
                .map(enchantment -> enchantment.getName().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        whenBrigadier(ctx -> {
            if (!ctx.hasNativeType() && ctx.getArgument().getFixedValues().isEmpty()) {
                ctx.getArgument().withFixedValues(values);
            }

            if (!ctx.hasNativeSuggestions()) {
                ctx.getArgument().includeSuggestions(values);
            }
        });

        whenLegacy(ctx -> {
            if (ctx.getArgument().getFixedValues().isEmpty()) {
                ctx.getArgument().withFixedValues(values);
            }

            ctx.getArgument().includeSuggestions(values);
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public Enchantment parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Enchantment enchantment = Enchantment.getByName(value.toUpperCase());

        if (enchantment == null) {
            throw ArgumentParseException.message(this, value, "Enchantment not found: " + value);
        }

        return enchantment;
    }

}
