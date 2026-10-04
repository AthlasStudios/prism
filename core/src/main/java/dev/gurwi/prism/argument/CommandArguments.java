package dev.gurwi.prism.argument;

import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandArguments<Definition extends AbstractCommandDefinition<Definition, ? super Source, ?, ?, ?>, Source> {

    private final Definition definition;
    private final Map<String, Object> requiredArgumentValues;
    private final Map<String, Object> optionalArgumentValues;
    private final @Nullable CommandArguments<Definition, Source> parent;

    public CommandArguments(@NotNull Definition definition, @NotNull Map<String, Object> requiredArgumentValues, @NotNull Map<String, Object> optionalArgumentValues, @Nullable CommandArguments<Definition, Source> parent) {
        this.definition = definition;
        this.requiredArgumentValues = Map.copyOf(requiredArgumentValues);
        this.optionalArgumentValues = Map.copyOf(optionalArgumentValues);
        this.parent = parent;
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

    public <T> @NotNull Optional<T> find(@NotNull AbstractArgument<?, T, Source, ?> argument) {
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

    public <T> T get(@NotNull AbstractArgument<?, T, Source, ?> argument) {
        return find(argument).orElseThrow(() -> new IllegalArgumentException("Argument not present: " + argument.getName()));
    }

    public <T> @NotNull Optional<T> getOptional(@NotNull String key) {
        return find(key);
    }

    public <T> @NotNull Optional<T> getOptional(@NotNull AbstractArgument<?, T, Source, ?> argument) {
        return find(argument);
    }

    public <T> T getOptional(@NotNull String key, T defaultValue) {
        return this.<T>find(key).orElse(defaultValue);
    }

    public <T> T getOptional(@NotNull AbstractArgument<?, T, Source, ?> argument, T defaultValue) {
        return find(argument).orElse(defaultValue);
    }

    private <T> @NotNull Optional<T> findAt(@NotNull String path, @NotNull String key) {
        return resolve(path).flatMap(node -> node.<T>local(key));
    }

    @SuppressWarnings("unchecked")
    private <T> @NotNull Optional<T> local(@NotNull String key) {
        if (requiredArgumentValues.containsKey(key)) {
            return Optional.ofNullable((T) requiredArgumentValues.get(key));
        }

        return Optional.ofNullable((T) optionalArgumentValues.get(key));
    }

    private boolean owns(@NotNull AbstractArgument<?, ?, Source, ?> argument) {
        return definition.getRequiredArguments().contains(argument) || definition.getOptionalArguments().contains(argument);
    }

}