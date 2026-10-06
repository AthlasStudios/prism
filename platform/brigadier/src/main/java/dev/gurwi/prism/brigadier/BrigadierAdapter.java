package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BrigadierAdapter<Source, Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    boolean visible(@NotNull Definition definition, @NotNull Source source);

    boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull Source source);

    @Nullable
    ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument);

    @Nullable Command<Source> command(@NotNull Definition definition);

    @Nullable SuggestionProvider<Source> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType);


}
