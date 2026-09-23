package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MaterialArgument extends BukkitArgument<MaterialArgument, Material> {

    public MaterialArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(Material.values())
                                .map(material -> material.name().toLowerCase())
                                .toList()
                )
        );
    }

    @Override
    public Material parse(@NotNull String value) throws ArgumentParseException {
        Material material = Material.matchMaterial(value);

        if (material == null) {
            throw new ArgumentParseException(this, value, "Material not found: " + value);
        }

        return material;
    }

}
