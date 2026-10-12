package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ColorArgument extends BukkitArgument<ColorArgument, Color> {

    private static final Map<String, Color> NAMED = new LinkedHashMap<>();

    static {
        NAMED.put("black", Color.fromRGB(0x000000));
        NAMED.put("dark_blue", Color.fromRGB(0x0000AA));
        NAMED.put("dark_green", Color.fromRGB(0x00AA00));
        NAMED.put("dark_aqua", Color.fromRGB(0x00AAAA));
        NAMED.put("dark_red", Color.fromRGB(0xAA0000));
        NAMED.put("dark_purple", Color.fromRGB(0xAA00AA));
        NAMED.put("gold", Color.fromRGB(0xFFAA00));
        NAMED.put("gray", Color.fromRGB(0xAAAAAA));
        NAMED.put("dark_gray", Color.fromRGB(0x555555));
        NAMED.put("blue", Color.fromRGB(0x5555FF));
        NAMED.put("green", Color.fromRGB(0x55FF55));
        NAMED.put("aqua", Color.fromRGB(0x55FFFF));
        NAMED.put("red", Color.fromRGB(0xFF5555));
        NAMED.put("light_purple", Color.fromRGB(0xFF55FF));
        NAMED.put("yellow", Color.fromRGB(0xFFFF55));
        NAMED.put("white", Color.fromRGB(0xFFFFFF));
    }

    public ColorArgument(String name) {
        super(name);
    }

    @Override
    public Color parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_');

        Color named = NAMED.get(normalized);
        if (named != null) {
            return named;
        }

        if (normalized.startsWith("#")) {
            String hex = normalized.substring(1);

            if (hex.length() == 6) {
                try {
                    return Color.fromRGB(Integer.parseInt(hex, 16));
                } catch (NumberFormatException ignored) {
                }
            }

            throw new ArgumentParseException(this, value, "Invalid hex color '" + value + "', expected #RRGGBB");
        }

        throw new ArgumentParseException(this, value, "Invalid color '" + value + "'. Use a name like dark_red, gold, aqua or a hex value like #ff5555");
    }

    @Contract(pure = true)
    public static @NotNull Iterable<String> names() {
        return NAMED.keySet();
    }

    public static @NotNull Optional<String> getNamed(Color color) {
        for (Map.Entry<String, Color> entry : NAMED.entrySet()) {
            if (entry.getValue().equals(color)) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

}