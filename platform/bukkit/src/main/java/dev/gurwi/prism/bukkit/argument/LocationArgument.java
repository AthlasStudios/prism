package dev.gurwi.prism.bukkit.argument;

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
import java.util.List;

public class LocationArgument extends MultiTokenArgument<LocationArgument, Location> {

    public LocationArgument(String name) {
        super(name, 3);

        replaceSuggestions((sender, input) -> {
            List<String> suggestions = new ArrayList<>();
            String rawInput = input == null ? "" : input;
            String trimmed = rawInput.trim();
            List<String> parts = trimmed.isEmpty() ? List.of() : List.of(trimmed.split("\\s+"));

            Block targetBlock = null;
            if (sender instanceof LivingEntity livingEntity) {
                targetBlock = livingEntity.getTargetBlockExact(5);
            }

            if (parts.isEmpty()) {
                if (targetBlock != null) {
                    suggestions.add(targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                }
                suggestions.add("~ ~ ~");
                suggestions.add("~");
                suggestions.add("^ ^ ^");
                return suggestions;
            }

            if (parts.size() >= 3) {
                return suggestions;
            }

            boolean isLocal = parts.get(0).startsWith("^");
            String symbol = isLocal ? "^" : "~";
            String typed = String.join(" ", parts);

            if (!isLocal && targetBlock != null) {
                int[] coordinates = { targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() };
                StringBuilder builder = new StringBuilder(typed);

                for (int i = parts.size(); i < 3; i++) {
                    builder.append(' ').append(coordinates[i]);
                }

                suggestions.add(builder.toString());
            }

            StringBuilder builder = new StringBuilder(typed);

            for (int i = parts.size(); i < 3; i++) {
                builder.append(' ').append(symbol);
            }

            suggestions.add(builder.toString());

            return suggestions;
        });
    }

    @Override
    public Location parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        String[] parts = value.trim().split("\\s+");

        if (parts.length != 3) {
            throw new ArgumentParseException(this, value, "Expected 3 coordinate components (X Y Z), got " + parts.length);
        }

        boolean isLocal = parts[0].startsWith("^");

        if (isLocal) {
            if (!parts[1].startsWith("^") || !parts[2].startsWith("^")) {
                throw new ArgumentParseException(this, value, "Cannot mix local (^) and non-local coordinates");
            }
        } else {
            if (parts[1].startsWith("^") || parts[2].startsWith("^")) {
                throw new ArgumentParseException(this, value, "Cannot mix local (^) and non-local coordinates");
            }
        }

        Location base = getSenderLocation(sender);
        boolean usesRelativeOrLocal = isLocal || parts[0].startsWith("~") || parts[1].startsWith("~") || parts[2].startsWith("~");

        if (usesRelativeOrLocal && base == null) {
            throw new ArgumentParseException(this, value, "Cannot use relative or local coordinates from a sender without a position");
        }

        if (isLocal) {
            double left = parseCoordToken(parts[0], 0);
            double up = parseCoordToken(parts[1], 0);
            double forward = parseCoordToken(parts[2], 0);
            return calculateLocalLocation(base, left, up, forward);
        }

        double x = parseCoordToken(parts[0], base != null ? base.getX() : 0);
        double y = parseCoordToken(parts[1], base != null ? base.getY() : 0);
        double z = parseCoordToken(parts[2], base != null ? base.getZ() : 0);

        World world = base != null ? base.getWorld() : (Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0));
        return new Location(world, x, y, z);
    }

    private double parseCoordToken(@NotNull String token, double baseVal) throws ArgumentParseException {
        try {
            if (token.startsWith("~") || token.startsWith("^")) {
                if (token.length() == 1) {
                    return baseVal;
                }
                return baseVal + Double.parseDouble(token.substring(1));
            }
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, token, "Invalid numeric coordinate: " + token);
        }
    }

    private @Nullable Location getSenderLocation(CommandSender sender) {
        if (sender instanceof Entity entity) {
            return entity.getLocation();
        } else if (sender instanceof BlockCommandSender blockSender) {
            Block block = blockSender.getBlock();
            return new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
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