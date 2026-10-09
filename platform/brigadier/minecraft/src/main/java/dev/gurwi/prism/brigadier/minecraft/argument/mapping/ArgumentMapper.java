package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ArgumentMapper<T, Argument extends BukkitArgument<?, T>> extends ArgumentValueMapper<Argument, T> {

    ArgumentType<?> toBrigadierType(Argument argument);

    static <T, Argument extends BukkitArgument<?, T>> @NotNull ArgumentMapper<T, Argument> create(Function<Argument, ArgumentType<?>> typeSupplier, ArgumentValueMapper<Argument, T> mapper) {
        return new ArgumentMapper<>() {

            @Override
            public T map(Argument argument, MinecraftCommandSource source, T value) {
                return mapper.map(argument, source, value);
            }

            @Override
            public ArgumentType<?> toBrigadierType(Argument argument) {
                return typeSupplier.apply(argument);
            }

        };
    }

    static <T, Argument extends BukkitArgument<?, T>> @NotNull ArgumentMapper<T, Argument> simple(Function<Argument, ArgumentType<?>> typeSupplier) {
        return new ArgumentMapper<>() {

            @Override
            public T map(Argument argument, MinecraftCommandSource source, T value) {
                return value;
            }

            @Override
            public ArgumentType<?> toBrigadierType(Argument argument) {
                return typeSupplier.apply(argument);
            }

        };
    }

}
