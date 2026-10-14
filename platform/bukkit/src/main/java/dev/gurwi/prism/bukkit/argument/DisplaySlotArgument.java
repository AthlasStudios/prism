package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DisplaySlotArgument extends BukkitArgument<DisplaySlotArgument, DisplaySlot> {

    public DisplaySlotArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(DisplaySlot.values())
                                .map(slot -> slot.name().toLowerCase())
                                .toList()
                )
        );

        forceSuggestions(true);
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