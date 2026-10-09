package dev.gurwi.prism.brigadier.minecraft.wrapper;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WrappedArgumentType<M, P> {

    private final ArgumentType<M> nmsBase;
    private final ResultConverter<M, P> converter;

    private WrappedArgumentType(ArgumentType<M> nmsBase, ResultConverter<M, P> converter) {
        this.nmsBase = nmsBase;
        this.converter = converter;
    }

    public ArgumentType<M> nativeNmsArgumentType() {
        return this.nmsBase;
    }

    public P parse(StringReader reader) throws CommandSyntaxException {
        return this.converter.convert(this.nmsBase.parse(reader));
    }

//    public <S> P parse(StringReader reader, S source) throws CommandSyntaxException {
//        return this.converter.convert(this.nmsBase.parse(reader, source));
//    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return this.nmsBase.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return this.nmsBase.getExamples();
    }

    @FunctionalInterface
    interface ResultConverter<T, R> {

        R convert(T base) throws CommandSyntaxException;

    }

}
