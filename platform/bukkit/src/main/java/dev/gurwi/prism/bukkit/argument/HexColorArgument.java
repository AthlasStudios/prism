package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HexColorArgument extends BukkitArgument<HexColorArgument, Color> {

    public HexColorArgument(String name) {
        super(name);
    }

    @Override
    public Color parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String hex = value.trim();

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 3 && hex.length() != 6) {
            throw new ArgumentParseException(this, value, "Incorrect hex format, should be 3 or 6 digits, got '" + value + "'");
        }

        for (int i = 0; i < hex.length(); i++) {
            if (Character.digit(hex.charAt(i), 16) == -1) {
                throw new ArgumentParseException(this, value, "Invalid hex digit '" + hex.charAt(i) + "' in '" + value + "'");
            }
        }

        if (hex.length() == 3) {
            int r = Character.digit(hex.charAt(0), 16);
            int g = Character.digit(hex.charAt(1), 16);
            int b = Character.digit(hex.charAt(2), 16);

            return Color.fromRGB(r * 17, g * 17, b * 17);
        }

        return Color.fromRGB(Integer.parseInt(hex, 16));
    }

}