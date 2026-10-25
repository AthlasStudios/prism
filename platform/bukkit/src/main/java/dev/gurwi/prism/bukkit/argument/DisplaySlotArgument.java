package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DisplaySlotArgument extends BukkitArgument<DisplaySlotArgument, DisplaySlot> {

    public DisplaySlotArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(DisplaySlot.values())
                .map(slot -> slot.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        includeSuggestionsUnlessNative(values);
        withFixedValuesUnlessNative(values);
    }

    @Override
    public DisplaySlot parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return DisplaySlot.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a display slot got " + value);
        }
    }

}