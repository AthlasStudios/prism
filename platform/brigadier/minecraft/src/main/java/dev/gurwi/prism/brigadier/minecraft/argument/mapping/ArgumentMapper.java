package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ArgumentMapper<T, BrigadierType, Argument extends BukkitArgument<?, T>> extends ArgumentValueMapper<Argument, T, BrigadierType> {

    ArgumentType<BrigadierType> toBrigadierType(Argument argument);

    static <T, BrigadierType, Argument extends BukkitArgument<?, T>> @NotNull ArgumentMapper<T, BrigadierType, Argument> constant(
            ArgumentType<BrigadierType> brigadierType,
            ArgumentValueMapper<Argument, T, BrigadierType> mapper
    ) {
        return create(argument -> brigadierType, mapper);
    }

    static <T, BrigadierType, Argument extends BukkitArgument<?, T>> @NotNull ArgumentMapper<T, BrigadierType, Argument> create(
            Function<Argument, ArgumentType<BrigadierType>> typeSupplier,
            ArgumentValueMapper<Argument, T, BrigadierType> mapper
    ) {
        return new ArgumentMapper<>() {
            @Override
            public T map(Argument argument, MinecraftCommandSource source, BrigadierType value) throws CommandSyntaxException {
                return mapper.map(argument, source, value);
            }

            @Override
            public ArgumentType<BrigadierType> toBrigadierType(Argument argument) {
                return typeSupplier.apply(argument);
            }
        };
    }

    static <T, Argument extends BukkitArgument<?, T>> @NotNull ArgumentMapper<T, T, Argument> simple(Function<Argument, ArgumentType<T>> typeSupplier) {
        return new ArgumentMapper<>() {
            @Override
            public T map(Argument argument, MinecraftCommandSource source, T value) {
                return value;
            }

            @Override
            public ArgumentType<T> toBrigadierType(Argument argument) {
                return typeSupplier.apply(argument);
            }
        };
    }

}