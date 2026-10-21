package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.parser.ParsedFlags;
import dev.gurwi.prism.bukkit.argument.suggestion.CommandSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.argument.type.Flag;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FlagArgument extends GreedyArgument<FlagArgument, ParsedFlags> {

    private final Map<String, Flag<?>> byLong = new LinkedHashMap<>();
    private final Map<Character, Flag<?>> byShort = new HashMap<>();

    public FlagArgument(String name) {
        super(name);

        replaceSuggestions(BukkitSuggestionsSupplier.async(this::suggest));
    }

    public FlagArgument flag(@NotNull Flag<?> flag) {
        if (byLong.putIfAbsent(flag.getName(), flag) != null) {
            throw new IllegalArgumentException("Duplicate flag: --" + flag.getName());
        }

        Character shortName = flag.getShortName().orElse(null);

        if (shortName != null && byShort.putIfAbsent(shortName, flag) != null) {
            byLong.remove(flag.getName());
            throw new IllegalArgumentException("Duplicate short flag: -" + shortName);
        }

        return self();
    }

    public FlagArgument flags(@NotNull Flag<?> @NotNull ... flags) {
        for (Flag<?> flag : flags) {
            flag(flag);
        }

        return self();
    }

    @Override
    public ParsedFlags parse(@NotNull CommandSender sender, @NotNull String input) throws ArgumentParseException {
        try {
            Tokenized tokenized = tokenize(input);
            Map<String, List<Object>> out = new LinkedHashMap<>();
            Cursor cursor = new Cursor(tokenized.tokens);

            while (cursor.hasNext()) {
                String token = cursor.next();

                if (token.startsWith("--")) {
                    parseLong(sender, token, cursor, out);
                } else if (token.startsWith("-") && token.length() > 1) {
                    parseShort(sender, token, cursor, out);
                } else {
                    throw error("Unexpected argument '" + token + "', expected a flag");
                }
            }

            return out.isEmpty() ? ParsedFlags.empty() : new ParsedFlags(out);
        } catch (FlagSyntaxException e) {
            throw new ArgumentParseException(this, input, e.getMessage());
        }
    }

    private void parseLong(CommandSender sender, @NotNull String token, Cursor cursor, Map<String, List<Object>> out) {
        String body = token.substring(2);
        String inline = null;
        int eq = body.indexOf('=');

        if (eq >= 0) {
            inline = body.substring(eq + 1);
            body = body.substring(0, eq);
        }

        Flag<?> flag = byLong.get(body.toLowerCase(Locale.ROOT));

        if (flag == null) {
            throw error("Unknown flag '--" + body + "'");
        }

        if (inline != null && flag.isPresence()) {
            throw error("Flag '--" + flag.getName() + "' does not take a value");
        }

        store(sender, flag, inline, cursor, out);
    }

    private void parseShort(CommandSender sender, @NotNull String token, Cursor cursor, Map<String, List<Object>> out) {
        String chars = token.substring(1);

        for (int i = 0; i < chars.length(); i++) {
            char c = Character.toLowerCase(chars.charAt(i));
            Flag<?> flag = byShort.get(c);

            if (flag == null) {
                throw error("Unknown flag '-" + c + "'");
            }

            if (!flag.isPresence() && i != chars.length() - 1) {
                throw error("Flag '-" + c + "' takes a value, so it must be last in '" + token + "'");
            }

            store(sender, flag, null, cursor, out);
        }
    }

    private void store(CommandSender sender, @NotNull Flag<?> flag, String inline, Cursor cursor, @NotNull Map<String, List<Object>> out) {
        List<Object> list = out.computeIfAbsent(flag.getName(), k -> new ArrayList<>());

        if (!list.isEmpty() && !flag.isRepeatable()) {
            throw error("Flag '--" + flag.getName() + "' was given more than once");
        }

        if (flag.isPresence()) {
            list.add(Boolean.TRUE);
            return;
        }

        String raw = inline != null ? inline : cursor.nextOrNull();

        if (raw == null) {
            throw error("Flag '--" + flag.getName() + "' needs a value");
        }

        try {
            list.add(flag.getValue().orElseThrow().parse(sender, raw));
        } catch (ArgumentParseException e) {
            throw error("Invalid value '" + raw + "' for '--" + flag.getName() + "': " + e.getMessage());
        }
    }

    public @NotNull CompletableFuture<Collection<CommandSuggestion>> suggest(@NotNull SuggestionContext<CommandSender> context) {
        String input = context.getCurrentRawArg();
        Tokenized tokenized = tokenize(input);
        String prefix = input.substring(0, tokenized.lastTokenStart);

        return suggestRelative(context, tokenized).thenApply(relative -> prefix.isEmpty() ? relative : withPrefix(relative, prefix));
    }

    private @NotNull CompletableFuture<Collection<CommandSuggestion>> suggestRelative(@NotNull SuggestionContext<CommandSender> context, @NotNull Tokenized tokenized) {
        List<String> tokens = new ArrayList<>(tokenized.tokens);
        String partial = "";

        if (!tokenized.trailingSpace && !tokens.isEmpty()) {
            partial = tokens.remove(tokens.size() - 1);
        }

        Set<String> used = new HashSet<>();
        Flag<?> pending = null;

        for (String token : tokens) {
            if (pending != null) {
                pending = null;

                continue;
            }

            if (token.startsWith("--")) {
                String body = token.substring(2);
                boolean inline = body.indexOf('=') >= 0;

                if (inline) {
                    body = body.substring(0, body.indexOf('='));
                }

                Flag<?> flag = byLong.get(body.toLowerCase(Locale.ROOT));

                if (flag != null) {
                    used.add(flag.getName());

                    if (!flag.isPresence() && !inline) {
                        pending = flag;
                    }
                }
            } else if (token.startsWith("-") && token.length() > 1) {
                String chars = token.substring(1);

                for (int i = 0; i < chars.length(); i++) {
                    Flag<?> flag = byShort.get(Character.toLowerCase(chars.charAt(i)));

                    if (flag == null) {
                        continue;
                    }

                    used.add(flag.getName());

                    if (!flag.isPresence() && i == chars.length() - 1) {
                        pending = flag;
                    }
                }
            }
        }

        if (pending != null) {
            return innerSuggestions(context, pending, partial);
        }

        if (partial.startsWith("--") && partial.indexOf('=') >= 0) {
            int eq = partial.indexOf('=');
            Flag<?> flag = byLong.get(partial.substring(2, eq).toLowerCase(Locale.ROOT));

            if (flag == null || flag.isPresence()) {
                return CompletableFuture.completedFuture(List.of());
            }

            String prefix = partial.substring(0, eq + 1);

            return innerSuggestions(context, flag, partial.substring(eq + 1)).thenApply(inner -> withPrefix(inner, prefix));
        }

        if (partial.isEmpty() || partial.startsWith("-")) {
            return CompletableFuture.completedFuture(flagSuggestions(used, partial));
        }

        return CompletableFuture.completedFuture(List.of());
    }

    private @NotNull List<CommandSuggestion> flagSuggestions(Set<String> used, @NotNull String partial) {
        String lowered = partial.toLowerCase(Locale.ROOT);
        List<CommandSuggestion> result = new ArrayList<>();

        for (Flag<?> flag : byLong.values()) {
            if (used.contains(flag.getName()) && !flag.isRepeatable()) {
                continue;
            }

            String description = flag.getDescription().orElse(null);

            if (flag.longLiteral().startsWith(lowered)) {
                result.add(suggestion(flag.longLiteral(), description));
            }

            String shortLiteral = flag.shortLiteral().orElse(null);

            if (!partial.isEmpty() && shortLiteral != null && shortLiteral.startsWith(lowered)) {
                result.add(suggestion(shortLiteral, description));
            }
        }

        return result;
    }

    private @NotNull CompletableFuture<Collection<CommandSuggestion>> innerSuggestions(@NotNull SuggestionContext<CommandSender> context, @NotNull Flag<?> flag, String partial) {
        BukkitArgument<?, ?> inner = flag.getValue().orElse(null);

        if (inner == null) {
            return CompletableFuture.completedFuture(List.of());
        }

        SuggestionContext<CommandSender> innerContext = new SuggestionContext<>(context.getSource(), context.getCurrentInput(), partial);
        String lowered = partial.toLowerCase(Locale.ROOT);

        if (inner.getFixedValues().isPresent()) {
            List<CommandSuggestion> fixed = new ArrayList<>();

            for (String value : inner.getFixedValues().get().get()) {
                fixed.add(CommandSuggestion.of(value));
            }

            return CompletableFuture.completedFuture(matching(fixed, lowered));
        }

        BukkitSuggestionsSupplier<?> supplier = inner.getSuggestionsSupplier();

        if (supplier instanceof BukkitSuggestionsSupplier.Async) {
            return ((BukkitSuggestionsSupplier.Async) supplier).getSuggestions(innerContext).thenApply(suggestions -> matching(suggestions, lowered));
        }

        if (supplier instanceof BukkitSuggestionsSupplier.Sync) {
            return CompletableFuture.completedFuture(matching(((BukkitSuggestionsSupplier.Sync) supplier).getSuggestions(innerContext), lowered));
        }

        return CompletableFuture.completedFuture(List.of());
    }

    private static @NotNull CommandSuggestion suggestion(@NotNull String literal, @Nullable String description) {
        if (description == null) {
            return CommandSuggestion.of(literal);
        }

        return CommandSuggestion.of(literal, description);
    }

    private static @NotNull List<CommandSuggestion> matching(@NotNull Collection<CommandSuggestion> suggestions, @NotNull String lowered) {
        List<CommandSuggestion> result = new ArrayList<>();

        for (CommandSuggestion suggestion : suggestions) {
            if (suggestion.getSuggestion().toLowerCase(Locale.ROOT).startsWith(lowered)) {
                result.add(suggestion);
            }
        }

        return result;
    }

    private static @NotNull List<CommandSuggestion> withPrefix(@NotNull Collection<CommandSuggestion> suggestions, @NotNull String prefix) {
        List<CommandSuggestion> result = new ArrayList<>(suggestions.size());

        for (CommandSuggestion suggestion : suggestions) {
            result.add(CommandSuggestion.of(prefix + suggestion.getSuggestion(), suggestion.getTooltip().orElse(null)));
        }

        return result;
    }

    @Contract("_ -> new")
    private static @NotNull Tokenized tokenize(@NotNull String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean hasToken = false;
        int tokenStart = input.length();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                if (!hasToken) {
                    tokenStart = i;
                }

                inQuotes = !inQuotes;
                hasToken = true;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (hasToken) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    hasToken = false;
                }
            } else {
                if (!hasToken) {
                    tokenStart = i;
                }

                current.append(c);
                hasToken = true;
            }
        }

        if (hasToken) {
            tokens.add(current.toString());
        }

        boolean trailingSpace = !input.isEmpty()
                && Character.isWhitespace(input.charAt(input.length() - 1))
                && !inQuotes;

        int lastTokenStart = (hasToken && !trailingSpace) ? tokenStart : input.length();

        return new Tokenized(tokens, trailingSpace, lastTokenStart);
    }

    private static final class Cursor {
        private final List<String> tokens;
        private int index;

        Cursor(List<String> tokens) {
            this.tokens = tokens;
        }

        boolean hasNext() {
            return index < tokens.size();
        }

        String next() {
            return tokens.get(index++);
        }

        @Nullable String nextOrNull() {
            return hasNext() ? next() : null;
        }
    }

    private static final class FlagSyntaxException extends RuntimeException {
        FlagSyntaxException(String message) {
            super(message, null, false, false);
        }
    }

    @Contract("_ -> new")
    private static @NotNull FlagSyntaxException error(String message) {
        return new FlagSyntaxException(message);
    }

    private static final class Tokenized {
        private final List<String> tokens;
        private final boolean trailingSpace;
        private final int lastTokenStart;

        private Tokenized(List<String> tokens, boolean trailingSpace, int lastTokenStart) {
            this.tokens = tokens;
            this.trailingSpace = trailingSpace;
            this.lastTokenStart = lastTokenStart;
        }

        public List<String> tokens() {
            return tokens;
        }

        public boolean trailingSpace() {
            return trailingSpace;
        }

        public int lastTokenStart() {
            return lastTokenStart;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Tokenized) obj;
            return Objects.equals(this.tokens, that.tokens) &&
                    this.trailingSpace == that.trailingSpace &&
                    this.lastTokenStart == that.lastTokenStart;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tokens, trailingSpace, lastTokenStart);
        }

        @Override
        public String toString() {
            return "Tokenized[" +
                    "tokens=" + tokens + ", " +
                    "trailingSpace=" + trailingSpace + ", " +
                    "lastTokenStart=" + lastTokenStart + ']';
        }

    }

}