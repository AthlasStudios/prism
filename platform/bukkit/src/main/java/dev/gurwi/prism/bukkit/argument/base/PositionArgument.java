package dev.gurwi.prism.bukkit.argument.base;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PositionArgument<Impl extends PositionArgument<Impl>> extends MultiTokenArgument<Impl, Location> {

    private final boolean column;
    private final boolean block;
    private final boolean centerIntegers;
    private final int components;

    protected PositionArgument(String name, boolean column, boolean block) {
        this(name, column, block, false);
    }

    protected PositionArgument(String name, boolean column, boolean block, boolean centerIntegers) {
        super(name, column ? 2 : 3);

        this.column = column;
        this.block = block;
        this.centerIntegers = centerIntegers;
        this.components = column ? 2 : 3;

        replaceSuggestions(ctx -> suggest(ctx.getSource(), ctx.getCurrentRawArg()));
    }

    @Override
    public Location parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] parts = value.trim().split("\\s+");

        if (parts.length != components) {
            throw new ArgumentParseException(this, value, "Expected " + components + " coordinate components, got " + parts.length);
        }

        boolean isLocal = parts[0].startsWith("^");

        for (String part : parts) {
            if (part.startsWith("^") != isLocal) {
                throw new ArgumentParseException(this, value, "Cannot mix local (^) and non-local coordinates");
            }
        }

        if (isLocal && column) {
            throw new ArgumentParseException(this, value, "Local coordinates are not supported here");
        }

        Location base = getSenderLocation(sender);
        boolean usesRelativeOrLocal = isLocal;

        for (String part : parts) {
            if (part.startsWith("~")) {
                usesRelativeOrLocal = true;
                break;
            }
        }

        if (usesRelativeOrLocal && base == null) {
            throw new ArgumentParseException(this, value, "Cannot use relative or local coordinates from a sender without a position");
        }

        Location result;

        if (isLocal) {
            double left = parseCoordToken(parts[0], 0, false);
            double up = parseCoordToken(parts[1], 0, false);
            double forward = parseCoordToken(parts[2], 0, false);
            result = calculateLocalLocation(base, left, up, forward);
        } else {
            double x = parseCoordToken(parts[0], base != null ? base.getX() : 0, centerIntegers);
            double y = column ? 0 : parseCoordToken(parts[1], base != null ? base.getY() : 0, false);
            double z = parseCoordToken(parts[components - 1], base != null ? base.getZ() : 0, centerIntegers);

            World world = base != null ? base.getWorld() : (Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0));
            result = new Location(world, x, y, z);
        }

        if (block) {
            return new Location(result.getWorld(), Math.floor(result.getX()), Math.floor(result.getY()), Math.floor(result.getZ()));
        }

        return result;
    }

    public boolean isColumn() {
        return column;
    }

    public boolean isBlock() {
        return block;
    }

    public boolean isCenterIntegers() {
        return centerIntegers;
    }

    private @NotNull List<String> suggest(@NotNull CommandSender sender, @Nullable String input) {
        List<String> suggestions = new ArrayList<>();
        String trimmed = input == null ? "" : input.trim();
        List<String> parts = trimmed.isEmpty() ? List.of() : List.of(trimmed.split("\\s+"));

        Block targetBlock = null;
        if (sender instanceof LivingEntity) {
            targetBlock = ((LivingEntity) sender).getTargetBlockExact(5);
        }

        int[] coordinates = null;
        if (targetBlock != null) {
            coordinates = column
                    ? new int[] { targetBlock.getX(), targetBlock.getZ() }
                    : new int[] { targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() };
        }

        if (parts.isEmpty()) {
            if (coordinates != null) {
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < components; i++) {
                    if (i > 0) {
                        builder.append(' ');
                    }

                    builder.append(coordinates[i]);
                }

                suggestions.add(builder.toString());
            }

            suggestions.add(String.join(" ", Collections.nCopies(components, "~")));
            suggestions.add("~");

            if (!column) {
                suggestions.add(String.join(" ", Collections.nCopies(components, "^")));
            }

            return suggestions;
        }

        if (parts.size() >= components) {
            return suggestions;
        }

        boolean isLocal = parts.get(0).startsWith("^");

        if (isLocal && column) {
            return suggestions;
        }

        String symbol = isLocal ? "^" : "~";
        String typed = String.join(" ", parts);

        if (!isLocal && coordinates != null) {
            StringBuilder builder = new StringBuilder(typed);

            for (int i = parts.size(); i < components; i++) {
                builder.append(' ').append(coordinates[i]);
            }

            suggestions.add(builder.toString());
        }

        StringBuilder builder = new StringBuilder(typed);

        for (int i = parts.size(); i < components; i++) {
            builder.append(' ').append(symbol);
        }

        suggestions.add(builder.toString());

        return suggestions;
    }

    private double parseCoordToken(@NotNull String token, double baseVal, boolean center) throws ArgumentParseException {
        try {
            if (token.startsWith("~") || token.startsWith("^")) {
                if (token.length() == 1) {
                    return baseVal;
                }

                return baseVal + Double.parseDouble(token.substring(1));
            }

            double parsed = Double.parseDouble(token);

            if (center && !token.contains(".")) {
                return parsed + 0.5;
            }

            return parsed;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, token, "Invalid numeric coordinate: " + token);
        }
    }

    private @Nullable Location getSenderLocation(@NotNull CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        }

        if (sender instanceof BlockCommandSender) {
            Block senderBlock = ((BlockCommandSender) sender).getBlock();
            return new Location(senderBlock.getWorld(), senderBlock.getX() + 0.5, senderBlock.getY() + 0.5, senderBlock.getZ() + 0.5);
        }

        return null;
    }

    @Contract("_, _, _, _ -> new")
    private @NotNull Location calculateLocalLocation(@NotNull Location base, double left, double up, double forward) {
        double yaw = Math.toRadians(base.getYaw());
        double pitch = Math.toRadians(base.getPitch());

        double sinYaw = Math.sin(yaw);
        double cosYaw = Math.cos(yaw);
        double sinPitch = Math.sin(pitch);
        double cosPitch = Math.cos(pitch);

        double lx = -cosYaw;
        double ly = 0;
        double lz = -sinYaw;

        double ux = -sinYaw * sinPitch;
        double uz = cosYaw * sinPitch;

        double fx = -sinYaw * cosPitch;
        double fy = -sinPitch;
        double fz = cosYaw * cosPitch;

        double x = base.getX() + left * lx + up * ux + forward * fx;
        double y = base.getY() + left * ly + up * cosPitch + forward * fy;
        double z = base.getZ() + left * lz + up * uz + forward * fz;

        return new Location(base.getWorld(), x, y, z, base.getYaw(), base.getPitch());
    }

}