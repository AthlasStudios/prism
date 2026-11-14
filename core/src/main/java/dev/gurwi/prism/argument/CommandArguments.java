package dev.gurwi.prism.argument;

import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CommandArguments<Definition extends AbstractCommandDefinition<Definition, ? super Source, ?, ?, ?>, Source> {

    private final Definition definition;
    private final Map<String, ParsedArgument<?>> requiredArgumentValues;
    private final Map<String, ParsedArgument<?>> optionalArgumentValues;
    private final @Nullable CommandArguments<Definition, Source> parent;
    private final String fullInput;

    public CommandArguments(@NotNull Definition definition, @NotNull Map<String, ParsedArgument<?>> requiredArgumentValues, @NotNull Map<String, ParsedArgument<?>> optionalArgumentValues, @NotNull String fullInput, @Nullable CommandArguments<Definition, Source> parent) {
        this.definition = definition;
        this.requiredArgumentValues = Map.copyOf(requiredArgumentValues);
        this.optionalArgumentValues = Map.copyOf(optionalArgumentValues);
        this.fullInput = fullInput;
        this.parent = parent;
    }

    public CommandArguments(@NotNull Definition definition, @NotNull Map<String, ParsedArgument<?>> requiredArgumentValues, @NotNull Map<String, ParsedArgument<?>> optionalArgumentValues, @NotNull String fullInput) {
        this(definition, requiredArgumentValues, optionalArgumentValues, fullInput, null);
    }

    public @NotNull String fullInput() {
        return fullInput;
    }

    public int count() {
        return requiredArgumentValues.size() + optionalArgumentValues.size();
    }

    public @NotNull Map<String, Object> argsMap() {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, ParsedArgument<?>> entry : ordered()) {
            result.put(entry.getKey(), entry.getValue().getParsed());
        }

        return Collections.unmodifiableMap(result);
    }

    public @NotNull Map<String, String> rawArgsMap() {
        Map<String, String> result = new LinkedHashMap<>();

        for (Map.Entry<String, ParsedArgument<?>> entry : ordered()) {
            result.put(entry.getKey(), entry.getValue().getRaw());
        }

        return Collections.unmodifiableMap(result);
    }

    public @NotNull List<Object> args() {
        List<Object> result = new ArrayList<>();

        for (Map.Entry<String, ParsedArgument<?>> entry : ordered()) {
            result.add(entry.getValue().getParsed());
        }

        return Collections.unmodifiableList(result);
    }

    public @NotNull String getRaw(@NotNull String key) {
        return getRawOptional(key).orElseThrow(() -> new IllegalArgumentException("Unknown argument: " + key));
    }

    public @NotNull String getRaw(@NotNull AbstractArgument<?, ?, ? extends Source, ?> argument) {
        return getRawOptional(argument).orElseThrow(() -> new IllegalArgumentException("Argument not present: " + argument.getName()));
    }

    public @NotNull String getRaw(int index) {
        return getRawOptional(index).orElseThrow(() -> new IndexOutOfBoundsException("Argument index out of range: " + index));
    }

    public @NotNull Optional<String> getRawOptional(@NotNull String key) {
        return localParsed(key).map(ParsedArgument::getRaw);
    }

    public @NotNull Optional<String> getRawOptional(@NotNull AbstractArgument<?, ?, ? extends Source, ?> argument) {
        return findParsed(argument).map(ParsedArgument::getRaw);
    }

    public @NotNull Optional<String> getRawOptional(int index) {
        return parsedAt(index).map(ParsedArgument::getRaw);
    }

    public @NotNull String getOrDefaultRaw(@NotNull String key, @NotNull String defaultValue) {
        return getRawOptional(key).orElse(defaultValue);
    }

    public @NotNull String getOrDefaultRaw(@NotNull AbstractArgument<?, ?, ? extends Source, ?> argument, @NotNull String defaultValue) {
        return getRawOptional(argument).orElse(defaultValue);
    }

    public @NotNull String getOrDefaultRaw(int index, @NotNull String defaultValue) {
        return getRawOptional(index).orElse(defaultValue);
    }

    public @NotNull Definition getDefinition() {
        return definition;
    }

    public @NotNull Optional<Definition> getParentDefinition() {
        return definition.getParent();
    }

    public @NotNull Definition getRootDefinition() {
        Definition current = definition;
        Optional<Definition> next = current.getParent();

        while (next.isPresent()) {
            current = next.get();
            next = current.getParent();
        }

        return current;
    }

    public @NotNull List<Definition> getDefinitionChain() {
        Deque<Definition> chain = new ArrayDeque<>();
        Optional<Definition> current = Optional.of(definition);

        while (current.isPresent()) {
            chain.addFirst(current.get());
            current = current.get().getParent();
        }

        return List.copyOf(chain);
    }

    public @NotNull List<Definition> getChildDefinitions() {
        return List.copyOf(definition.getChildren());
    }

    public @NotNull Optional<Definition> getChildDefinition(@NotNull String name) {
        return definition.getChild(name.toLowerCase());
    }

    public @NotNull Optional<Definition> resolveDefinition(@NotNull String path) {
        Optional<Definition> current = Optional.of(path.startsWith("/") ? getRootDefinition() : definition);

        for (String segment : path.split("/")) {
            if (current.isEmpty()) {
                break;
            }

            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }

            current = segment.equals("..")
                    ? current.flatMap(AbstractCommandDefinition::getParent)
                    : current.flatMap(node -> node.getChild(segment.toLowerCase()));
        }

        return current;
    }

    public @NotNull Optional<CommandArguments<Definition, Source>> getParent() {
        return Optional.ofNullable(parent);
    }

    public @NotNull CommandArguments<Definition, Source> getRoot() {
        CommandArguments<Definition, Source> current = this;

        while (current.parent != null) {
            current = current.parent;
        }

        return current;
    }

    public @NotNull List<CommandArguments<Definition, Source>> getChain() {
        Deque<CommandArguments<Definition, Source>> chain = new ArrayDeque<>();
        CommandArguments<Definition, Source> current = this;

        while (current != null) {
            chain.addFirst(current);
            current = current.parent;
        }

        return List.copyOf(chain);
    }

    public @NotNull Optional<CommandArguments<Definition, Source>> resolve(@NotNull String path) {
        List<CommandArguments<Definition, Source>> chain = getChain();
        int index = path.startsWith("/") ? 0 : chain.size() - 1;

        for (String segment : path.split("/")) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }

            if (segment.equals("..")) {
                index--;

                if (index < 0) {
                    return Optional.empty();
                }

                continue;
            }

            index++;

            if (index >= chain.size() || !chain.get(index).definition.getName().equalsIgnoreCase(segment)) {
                return Optional.empty();
            }
        }

        return Optional.of(chain.get(index));
    }

    public <T> T getAt(@NotNull String path, @NotNull String key) {
        return this.<T>findAt(path, key).orElseThrow(() -> new IllegalArgumentException("Unknown argument '" + key + "' at path: " + path));
    }

    public <T> T getAt(@NotNull String path, @NotNull AbstractArgument<?, T, Source, ?> argument) {
        return this.<T>findAt(path, argument.getName()).orElseThrow(() -> new IllegalArgumentException("Argument '" + argument.getName() + "' not present at path: " + path));
    }

    public <T> @NotNull Optional<T> getOptionalAt(@NotNull String path, @NotNull String key) {
        return findAt(path, key);
    }

    public <T> @NotNull Optional<T> getOptionalAt(@NotNull String path, @NotNull AbstractArgument<?, T, Source, ?> argument) {
        return findAt(path, argument.getName());
    }

    public <T> T getOptionalAt(@NotNull String path, @NotNull String key, T defaultValue) {
        return this.<T>findAt(path, key).orElse(defaultValue);
    }

    public <T> T getOptionalAt(@NotNull String path, @NotNull AbstractArgument<?, T, Source, ?> argument, T defaultValue) {
        return this.<T>findAt(path, argument.getName()).orElse(defaultValue);
    }

    public <T> @NotNull Optional<T> find(@NotNull String key) {
        return local(key);
    }

    public <T> @NotNull Optional<T> find(@NotNull AbstractArgument<?, T, ? extends Source, ?> argument) {
        CommandArguments<Definition, Source> current = this;

        while (current != null) {
            if (current.owns(argument)) {
                return current.local(argument.getName());
            }

            current = current.parent;
        }

        return Optional.empty();
    }

    public <T> T get(@NotNull String key) {
        return this.<T>find(key).orElseThrow(() -> new IllegalArgumentException("Unknown argument: " + key));
    }

    public <T> T get(@NotNull AbstractArgument<?, T, ? extends Source, ?> argument) {
        return find(argument).orElseThrow(() -> new IllegalArgumentException("Argument not present: " + argument.getName()));
    }

    public <T> @NotNull Optional<T> getOptional(@NotNull String key) {
        return find(key);
    }

    public <T> @NotNull Optional<T> getOptional(@NotNull AbstractArgument<?, T, ? extends Source, ?> argument) {
        return find(argument);
    }

    public <T> T get(int index) {
        return this.<T>getOptional(index).orElseThrow(() -> new IndexOutOfBoundsException("Argument index out of range: " + index));
    }

    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        return cast(this.get(key), type, key);
    }

    public <T> T get(int index, @NotNull Class<T> type) {
        return cast(this.get(index), type, String.valueOf(index));
    }

    public <T> @NotNull Optional<T> getOptional(int index) {
        return parsedAt(index).map(parsed -> {
            @SuppressWarnings("unchecked")
            T value = (T) parsed.getParsed();
            return value;
        });
    }

    public <T> @NotNull Optional<T> getOptional(@NotNull String key, @NotNull Class<T> type) {
        return this.find(key).map(value -> cast(value, type, key));
    }

    public <T> @NotNull Optional<T> getOptional(int index, @NotNull Class<T> type) {
        return this.getOptional(index).map(value -> cast(value, type, String.valueOf(index)));
    }

    public <T> T getOptional(int index, T defaultValue) {
        return this.<T>getOptional(index).orElse(defaultValue);
    }

    public <T> T getOptional(@NotNull String key, @NotNull Supplier<? extends T> defaultSupplier) {
        return this.<T>find(key).orElseGet(defaultSupplier);
    }

    public <T> T getOptional(@NotNull AbstractArgument<?, T, ? extends Source, ?> argument, @NotNull Supplier<? extends T> defaultSupplier) {
        return find(argument).orElseGet(defaultSupplier);
    }

    public <T> T getOptional(int index, @NotNull Supplier<? extends T> defaultSupplier) {
        return this.<T>getOptional(index).orElseGet(defaultSupplier);
    }

    public <T> T getOptional(@NotNull String key, T defaultValue) {
        return this.<T>find(key).orElse(defaultValue);
    }

    public <T> T getOptional(@NotNull AbstractArgument<?, T, ? extends Source, ?> argument, T defaultValue) {
        return find(argument).orElse(defaultValue);
    }

    private <T> @NotNull Optional<T> findAt(@NotNull String path, @NotNull String key) {
        return resolve(path).flatMap(node -> node.local(key));
    }

    @SuppressWarnings("unchecked")
    private <T> @NotNull Optional<T> local(@NotNull String key) {
        return localParsed(key).map(parsed -> (T) parsed.getParsed());
    }

    private @NotNull Optional<ParsedArgument<?>> localParsed(@NotNull String key) {
        ParsedArgument<?> parsed = requiredArgumentValues.get(key);

        if (parsed == null) {
            parsed = optionalArgumentValues.get(key);
        }

        return Optional.ofNullable(parsed);
    }

    private @NotNull Optional<ParsedArgument<?>> findParsed(@NotNull AbstractArgument<?, ?, ? extends Source, ?> argument) {
        CommandArguments<Definition, Source> current = this;

        while (current != null) {
            if (current.owns(argument)) {
                return current.localParsed(argument.getName());
            }

            current = current.parent;
        }

        return Optional.empty();
    }

    private @NotNull List<Map.Entry<String, ParsedArgument<?>>> ordered() {
        List<Map.Entry<String, ParsedArgument<?>>> result = new ArrayList<>();

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getRequiredArguments()) {
            ParsedArgument<?> parsed = requiredArgumentValues.get(argument.getName());

            if (parsed != null) {
                result.add(Map.entry(argument.getName(), parsed));
            }
        }

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getOptionalArguments()) {
            ParsedArgument<?> parsed = optionalArgumentValues.get(argument.getName());

            if (parsed != null) {
                result.add(Map.entry(argument.getName(), parsed));
            }
        }

        for (Map.Entry<String, ParsedArgument<?>> entry : requiredArgumentValues.entrySet()) {
            if (result.stream().noneMatch(existing -> existing.getKey().equals(entry.getKey()))) {
                result.add(entry);
            }
        }

        for (Map.Entry<String, ParsedArgument<?>> entry : optionalArgumentValues.entrySet()) {
            if (result.stream().noneMatch(existing -> existing.getKey().equals(entry.getKey()))) {
                result.add(entry);
            }
        }

        return result;
    }

    private @NotNull Optional<ParsedArgument<?>> parsedAt(int index) {
        List<Map.Entry<String, ParsedArgument<?>>> entries = ordered();

        if (index < 0 || index >= entries.size()) {
            return Optional.empty();
        }

        return Optional.of(entries.get(index).getValue());
    }

    private static <T> T cast(Object value, @NotNull Class<T> type, @NotNull String key) {
        if (value == null) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Argument '" + key + "' is of type " + value.getClass().getName() + ", not " + type.getName());
        }

        return type.cast(value);
    }

    private boolean owns(@NotNull AbstractArgument<?, ?, ? extends Source, ?> argument) {
        return definition.getRequiredArguments().contains(argument) || definition.getOptionalArguments().contains(argument);
    }

}