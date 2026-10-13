package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface BrigadierAdapter<Source, Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    boolean visible(@NotNull Definition definition, @NotNull Source source);

    boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull Source source);

    @Nullable
    ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    default @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> branches(@NotNull Definition definition) {
        return List.of();
    }

    default @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> children(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return List.of();
    }

    default boolean literal(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return false;
    }

    default @Nullable Command<Source> command(@NotNull Definition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path) {
        return null;
    }

    default boolean forced(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return false;
    }

    default @NotNull List<String> forcedValues(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return List.of();
    }

    default @Nullable Command<Source> command(@NotNull Definition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        return command(definition, path);
    }

    default boolean accepts(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull Source source, @NotNull String word) {
        return true;
    }

    @Nullable Command<Source> command(@NotNull Definition definition);

    @Nullable SuggestionProvider<Source> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType);

}