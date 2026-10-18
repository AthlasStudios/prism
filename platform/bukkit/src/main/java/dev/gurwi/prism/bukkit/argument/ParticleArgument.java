package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ParticleArgument extends BukkitArgument<ParticleArgument, Particle> {

    public ParticleArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Particle.values())
                .map(material -> material.name().toLowerCase())
                .toList();

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    public Particle parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Particle.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Particle not found: " + value);
        }
    }

}
