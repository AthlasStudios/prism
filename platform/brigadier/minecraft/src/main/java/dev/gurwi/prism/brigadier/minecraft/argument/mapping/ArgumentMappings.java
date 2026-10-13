package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.argument.MinecraftArgumentType;
import dev.gurwi.prism.brigadier.minecraft.exception.UnsupportedArgumentTypeException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.argument.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ArgumentMappings {

    public static final ArgumentMappings INSTANCE = new ArgumentMappings();

    private final Map<Class<?>, Binder> binders = new ConcurrentHashMap<>();

    private ArgumentMappings() {
        register(StringArgument.class, ArgumentMapper.simple(arg -> StringArgumentType.string()));
        register(GreedyStringArgument.class, ArgumentMapper.simple(arg -> StringArgumentType.greedyString()));
        register(IntegerArgument.class, ArgumentMapper.simple(arg -> IntegerArgumentType.integer(arg.getMin(), arg.getMax())));
        register(DoubleArgument.class, ArgumentMapper.simple(arg -> DoubleArgumentType.doubleArg(arg.getMin(), arg.getMax())));
        register(FloatArgument.class, ArgumentMapper.simple(arg -> FloatArgumentType.floatArg(arg.getMin(), arg.getMax())));
        register(LongArgument.class, ArgumentMapper.simple(arg -> LongArgumentType.longArg(arg.getMin(), arg.getMax())));
        register(BooleanArgument.class, ArgumentMapper.simple(arg -> BoolArgumentType.bool()));

        register(GameModeArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.gameMode()));
        register(ParticleArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.particle()));
        register(ColorArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.color()));
        register(HexColorArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.hexColor()));
        register(UUIDArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.uuid()));
        register(IntegerRangeArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.integerRange()));
        register(DoubleRangeArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.doubleRange()));
        register(FloatRangeArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.floatRange()));

        register(EntitySelectorArgument.OneEntity.class, ArgumentMapper.constant(
                MinecraftArgumentType.entity(),
                (argument, source, resolver) -> resolver.resolve(source).get(0)
        ));

        register(EntitySelectorArgument.ManyEntities.class, ArgumentMapper.constant(
                MinecraftArgumentType.entities(),
                (argument, source, resolver) -> resolver.resolve(source)
        ));

        register(EntitySelectorArgument.OnePlayer.class, ArgumentMapper.constant(
                MinecraftArgumentType.player(),
                (argument, source, resolver) -> resolver.resolve(source).get(0)
        ));

        register(EntitySelectorArgument.ManyPlayers.class, ArgumentMapper.constant(
                MinecraftArgumentType.players(),
                (argument, source, resolver) -> resolver.resolve(source)
        ));

        register(LocationArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.finePosition(),
                (argument, source, resolver) -> resolver.resolve(source).toLocation(source.getLocation().getWorld())
        ));

        register(MaterialArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.itemStack(),
                (argument, source, item) -> item.getType()
        ));
    }

    public <T, BrigadierType, Argument extends BukkitArgument<?, T>> void register(@NotNull Class<Argument> type, @NotNull ArgumentMapper<T, BrigadierType, Argument> mapper) {
        binders.put(type, argument -> bind(mapper, type.cast(argument)));
    }

    public <T> @NotNull Optional<BoundArgumentMapper<T>> find(@NotNull BukkitArgument<?, T> argument) {
        try {
            return Optional.ofNullable(resolve(argument));
        } catch (UnsupportedArgumentTypeException exception) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> @Nullable BoundArgumentMapper<T> resolve(@NotNull BukkitArgument<?, T> argument) {
        if (argument instanceof ArgumentMapper<?, ?, ?> self) {
            return bindSelf(self, argument);
        }

        Class<?> type = argument.getClass();

        while (type != null && type != Object.class) {
            Binder binder = binders.get(type);

            if (binder != null) {
                return (BoundArgumentMapper<T>) binder.bind(argument);
            }

            type = type.getSuperclass();
        }

        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> @NotNull BoundArgumentMapper<T> bindSelf(ArgumentMapper<?, ?, ?> mapper, BukkitArgument<?, T> argument) {
        return bind((ArgumentMapper) mapper, argument);
    }

    private static <T, BrigadierType, Argument extends BukkitArgument<?, T>> @NotNull BoundArgumentMapper<T> bind(@NotNull ArgumentMapper<T, BrigadierType, Argument> mapper, Argument argument) {
        ArgumentType<BrigadierType> type = mapper.toBrigadierType(argument);

        return new BoundArgumentMapper<>() {
            @Override
            public ArgumentType<?> type() {
                return type;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T read(CommandContext<MinecraftCommandSource> context, String node, MinecraftCommandSource source) throws CommandSyntaxException {
                return mapper.map(argument, source, (BrigadierType) context.getArgument(node, Object.class));
            }
        };
    }

    private interface Binder {

        BoundArgumentMapper<?> bind(BukkitArgument<?, ?> argument);

    }

}