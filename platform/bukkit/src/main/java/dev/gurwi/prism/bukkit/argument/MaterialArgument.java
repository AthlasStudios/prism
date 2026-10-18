package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MaterialArgument extends BukkitArgument<MaterialArgument, Material> {

    public MaterialArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Material.values())
                .map(material -> material.name().toLowerCase())
                .toList();

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    public Material parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Material material = Material.matchMaterial(value);

        if (material == null) {
            throw new ArgumentParseException(this, value, "Material not found: " + value);
        }

        return material;
    }

}
