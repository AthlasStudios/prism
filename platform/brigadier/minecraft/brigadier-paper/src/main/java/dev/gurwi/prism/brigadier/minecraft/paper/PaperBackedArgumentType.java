package dev.gurwi.prism.brigadier.minecraft.paper;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class PaperBackedArgumentType<Native, Argument> implements CustomArgumentType<Argument, Native> {

    private final ArgumentType<Native> nativeType;
    private final Function<Native, Argument> converter;

    private PaperBackedArgumentType(ArgumentType<Native> nativeType, Function<Native, Argument> converter) {
        this.nativeType = nativeType;
        this.converter = converter;
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <N, A> @NotNull PaperBackedArgumentType<N, A> of(ArgumentType<N> nativeType, Function<N, A> converter) {
        return new PaperBackedArgumentType<>(nativeType, converter);
    }

    @Override
    public @NotNull ArgumentType<Native> getNativeType() {
        return nativeType;
    }

    @Override
    public @NotNull Argument parse(@NotNull StringReader reader) throws CommandSyntaxException {
        return converter.apply(nativeType.parse(reader));
    }

}