package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.gurwi.prism.brigadier.NativeTypeHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class NativeBackedArgumentType<Native, Argument> implements ArgumentType<Argument>, NativeTypeHolder {

    private final ArgumentType<Native> nativeType;
    private final ArgumentResultConverter<Native, Argument> converter;

    private NativeBackedArgumentType(ArgumentType<Native> nativeType, ArgumentResultConverter<Native, Argument> converter) {
        this.nativeType = nativeType;
        this.converter = converter;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static <Native, Argument> @NotNull NativeBackedArgumentType<Native, Argument> of(ArgumentType<Native> nativeType, ArgumentResultConverter<Native, Argument> converter) {
        return new NativeBackedArgumentType<>(nativeType, converter);
    }

    @Override
    public @NotNull ArgumentType<Native> getNativeType() {
        return nativeType;
    }

    @SuppressWarnings("unchecked")
    public Object convertNative(Object value) throws CommandSyntaxException {
        return converter.apply((Native) value);
    }

    @Override
    public Argument parse(StringReader reader) throws CommandSyntaxException {
        return converter.apply(nativeType.parse(reader));
    }

    @Override
    public <Source> CompletableFuture<Suggestions> listSuggestions(CommandContext<Source> context, SuggestionsBuilder builder) {
        try {
            return nativeType.listSuggestions(context, builder);
        } catch (RuntimeException exception) {
            return builder.buildFuture();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return nativeType.getExamples();
    }

}