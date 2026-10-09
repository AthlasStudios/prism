package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.*;
import dev.gurwi.prism.bukkit.argument.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArgumentMappings {

    public static final ArgumentMappings INSTANCE = new ArgumentMappings();

    private final Map<Class<? extends BukkitArgument<?, ?>>, ArgumentMapper<?, ?>> argumentMappings = new HashMap<>();

    private ArgumentMappings() {
        register(StringArgument.class, ArgumentMapper.simple(arg -> StringArgumentType.string()));
        register(GreedyStringArgument.class, ArgumentMapper.simple(arg -> StringArgumentType.greedyString()));
        register(IntegerArgument.class, ArgumentMapper.simple(arg -> IntegerArgumentType.integer(arg.getMin(), arg.getMax())));
        register(DoubleArgument.class, ArgumentMapper.simple(arg -> DoubleArgumentType.doubleArg(arg.getMin(), arg.getMax())));
        register(FloatArgument.class, ArgumentMapper.simple(arg -> FloatArgumentType.floatArg(arg.getMin(), arg.getMax())));
        register(LongArgument.class, ArgumentMapper.simple(arg -> LongArgumentType.longArg(arg.getMin(), arg.getMax())));
        register(BooleanArgument.class, ArgumentMapper.simple(arg -> BoolArgumentType.bool()));
    }

    public <T, Argument extends BukkitArgument<?, T>> void register(@NotNull Class<Argument> type, @NotNull ArgumentMapper<T, Argument> mapping) {
        argumentMappings.put(type, mapping);
    }

    @SuppressWarnings("unchecked")
    public <T, Argument extends BukkitArgument<?, T>> Optional<ArgumentMapper<T, Argument>> find(@NotNull Argument argument) {
        Class<?> type = argument.getClass();

        while (type != null && type != Object.class) {
            ArgumentMapper<?, ?> mapping = argumentMappings.get(type);

            if (mapping != null) {
                return Optional.of((ArgumentMapper<T, Argument>) mapping);
            }

            type = type.getSuperclass();
        }

        return Optional.empty();
    }

}
