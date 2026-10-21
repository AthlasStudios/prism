package dev.gurwi.prism.bukkit.argument.base;

import dev.gurwi.prism.bukkit.argument.suggestion.CommandSuggestion;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class EntitySelectorArgument<Impl extends EntitySelectorArgument<Impl, T>, T> extends BukkitArgument<Impl, T> {

    private static final List<String> SELECTORS = List.of("@a", "@e", "@p", "@r", "@s");

    private static final List<String> PARAMETERS = List.of("type=", "distance=", "x=", "y=", "z=", "dx=", "dy=", "dz=", "limit=", "sort=", "name=", "tag=", "team=", "gamemode=", "level=");

    private static final List<String> SORTS = List.of("nearest", "furthest", "random", "arbitrary");

    protected EntitySelectorArgument(@NotNull String name) {
        super(name);

        replaceSuggestions(ctx -> CommandSuggestion.of(getSuggestions(ctx.getSource(), ctx.getCurrentRawArg())));
    }

    private static @NotNull List<String> getSuggestions(@NotNull CommandSender sender, @NotNull String input) {
        if (input.isEmpty()) {
            return getInitialSuggestions();
        }

        if (!input.startsWith("@")) {
            return getPlayerSuggestions("", input);
        }

        int bracket = input.indexOf('[');

        if (bracket == -1) {
            return getSelectorSuggestions(input);
        }

        return getParameterSuggestions(input, bracket);
    }

    private static @NotNull List<String> getInitialSuggestions() {
        List<String> suggestions = new ArrayList<>(SELECTORS);

        suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        return suggestions;
    }

    private static @NotNull List<String> getSelectorSuggestions(@NotNull String input) {
        List<String> suggestions = new ArrayList<>();

        for (String selector : SELECTORS) {
            if (selector.startsWith(input)) {
                suggestions.add(selector);
                suggestions.add(selector + "[");
            }
        }

        return suggestions;
    }

    private static @NotNull List<String> getParameterSuggestions(@NotNull String input, int bracket) {
        String prefix = input.substring(0, bracket + 1);
        String arguments = input.substring(bracket + 1);

        if (arguments.isEmpty()) {
            return getParameterNames(prefix, "");
        }

        int comma = arguments.lastIndexOf(',');

        if (comma == -1) {
            return getCurrentParameterSuggestions(prefix, "", arguments);
        }

        String previous = arguments.substring(0, comma + 1);
        String current = arguments.substring(comma + 1);

        return getCurrentParameterSuggestions(prefix, previous, current);
    }

    private static @NotNull List<String> getCurrentParameterSuggestions(@NotNull String prefix, @NotNull String previous, @NotNull String current) {
        int equals = current.indexOf('=');

        if (equals == -1) {
            return getParameterNames(prefix + previous, current);
        }

        String parameter = current.substring(0, equals + 1);
        String value = current.substring(equals + 1);

        return getParameterValues(prefix + previous + parameter, parameter, value);
    }

    private static @NotNull List<String> getParameterNames(@NotNull String prefix, @NotNull String input) {
        List<String> suggestions = new ArrayList<>();

        for (String parameter : PARAMETERS) {
            String suggestion = prefix + parameter;

            if (suggestion.startsWith(prefix + input)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    private static @NotNull List<String> getParameterValues(@NotNull String prefix, @NotNull String parameter, @NotNull String input) {
        switch (parameter) {
            case "gamemode=":
                return getGameModeSuggestions(prefix, input);
            case "sort=":
                return filter(prefix, input, SORTS);
            case "type=":
                return getEntityTypeSuggestions(prefix, input);
            case "name=":
                return getPlayerSuggestions(prefix, input);
            case "team=":
                return getTeamSuggestions(prefix, input);
            case "tag=":
                return getTagSuggestions(prefix, input);
            default:
                return List.of();
        }
    }

    private static @NotNull List<String> getGameModeSuggestions(@NotNull String prefix, @NotNull String input) {
        return Arrays.stream(GameMode.values())
                .map(mode -> mode.name().toLowerCase(Locale.ROOT))
                .filter(mode -> startsWith(mode, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull List<String> getEntityTypeSuggestions(@NotNull String prefix, @NotNull String input) {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::getName)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(type -> startsWith(type, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull List<String> getTeamSuggestions(@NotNull String prefix, @NotNull String input) {
        if (Bukkit.getScoreboardManager() == null) {
            return List.of();
        }

        return Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
                .map(Team::getName)
                .filter(team -> startsWith(team, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull @Unmodifiable List<String> getTagSuggestions(@NotNull String prefix, @NotNull String input) {
        return Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .flatMap(entity -> entity.getScoreboardTags().stream())
                .distinct()
                .filter(tag -> startsWith(tag, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull @Unmodifiable List<String> getPlayerSuggestions(@NotNull String prefix, @NotNull String input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> startsWith(name, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull @Unmodifiable List<String> filter(@NotNull String prefix, @NotNull String input, @NotNull Collection<String> values) {
        return values.stream()
                .filter(value -> startsWith(value, input))
                .map(prefix::concat)
                .collect(Collectors.toUnmodifiableList());
    }

    private static boolean startsWith(@NotNull String value, @NotNull String input) {
        return value.regionMatches(true, 0, input, 0, input.length());
    }

    public static class OneEntity extends EntitySelectorArgument<OneEntity, Entity> {

        public OneEntity(@NotNull String name) {
            super(name);
        }

        @Override
        public @NotNull Entity parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
            return parseEntity(this, sender, value);
        }
    }

    public static class ManyEntities extends EntitySelectorArgument<ManyEntities, Collection<Entity>> {

        public ManyEntities(@NotNull String name) {
            super(name);
        }

        @Override
        public @NotNull Collection<Entity> parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
            return parseEntities(this, sender, value);
        }
    }

    public static class OnePlayer extends EntitySelectorArgument<OnePlayer, Player> {

        public OnePlayer(@NotNull String name) {
            super(name);
        }

        @Override
        public @NotNull Player parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
            Entity entity = parseEntity(this, sender, value);

            if (!(entity instanceof Player)) {
                throw new ArgumentParseException(this, value, "Expected a player");
            }

            return (Player) entity;
        }
    }

    public static class ManyPlayers extends EntitySelectorArgument<ManyPlayers, Collection<Player>> {

        public ManyPlayers(@NotNull String name) {
            super(name);
        }

        @Override
        public @NotNull Collection<Player> parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
            Collection<Entity> entities = parseEntities(this, sender, value);

            List<Player> players = new ArrayList<>(entities.size());

            for (Entity entity : entities) {
                if (!(entity instanceof Player)) {
                    throw new ArgumentParseException(this, value, "Expected only players");
                }

                players.add((Player) entity);
            }

            return players;
        }
    }

    private static @NotNull Entity parseEntity(@NotNull EntitySelectorArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Collection<Entity> entities = parseEntities(argument, sender, value);

        if (entities.size() != 1) {
            throw new ArgumentParseException(argument, value, "Expected exactly one entity");
        }

        return entities.iterator().next();
    }

    private static @NotNull Collection<Entity> parseEntities(@NotNull EntitySelectorArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            Collection<Entity> entities = Bukkit.selectEntities(sender, value);

            if (entities.isEmpty()) {
                throw new ArgumentParseException(argument, value, "No entities matched");
            }

            return entities;
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(argument, value, e.getMessage() != null ? e.getMessage() : "Invalid entity selector");
        }
    }

}