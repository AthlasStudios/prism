package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.argument.MinecraftArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.ColumnFinePositionResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.FinePositionResolver;
import dev.gurwi.prism.brigadier.minecraft.exception.UnsupportedArgumentTypeException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedColumnBlockPosition;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedColumnFinePosition;
import dev.gurwi.prism.bukkit.AngleArgument;
import dev.gurwi.prism.bukkit.argument.*;
import dev.gurwi.prism.bukkit.argument.type.Rotation;
import org.bukkit.Location;
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
        register(AxesArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.axes()));
        register(MessageArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.message()));
        register(DisplaySlotArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.scoreboardDisplaySlot()));
        register(NamespacedKeyArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.namespacedKey()));
        register(WorldArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.world()));
        register(TimeArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.time(arg.getMin())));
        register(MirrorArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.templateMirror()));
        register(StructureRotationArgument.class, ArgumentMapper.simple(arg -> MinecraftArgumentType.templateRotation()));

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

//        register(PlayerProfilesArgument.class, ArgumentMapper.constant(
//                MinecraftArgumentType.playerProfiles(),
//                (argument, source, resolver) -> resolver.resolve(source)
//        ));

        register(LocationArgument.class, ArgumentMapper.<Location, FinePositionResolver, LocationArgument>create(
                argument -> MinecraftArgumentType.finePosition(argument.isCenterIntegers()),
                (argument, source, resolver) -> resolver.resolve(source).toLocation(source.getLocation().getWorld())
        ));

        register(MaterialArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.itemStack(),
                (argument, source, item) -> item.getType()
        ));

        register(BlockPositionArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.blockPosition(),
                (argument, source, resolver) -> resolver.resolve(source).toLocation(source.getLocation().getWorld())
        ));

        register(RotationArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.rotation(),
                (argument, source, resolver) -> {
                    var rotation = resolver.resolve(source);
                    return new Rotation(rotation.yaw(), rotation.pitch());
                }
        ));

        register(AngleArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.angle(),
                (argument, source, resolver) -> resolver.resolve(source)
        ));

        register(BlockStateArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.blockState(),
                (argument, source, state) -> state.getBlockData()
        ));

        register(ItemStackPredicateArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.itemStackPredicate(),
                (argument, source, predicate) -> predicate
        ));

        register(BlockPredicateArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.blockInWorldPredicate(),
                (argument, source, predicate) -> predicate.toBukkit()
        ));

        register(ColumnLocationArgument.class, ArgumentMapper.<Location, ColumnFinePositionResolver, ColumnLocationArgument>create(
                argument -> MinecraftArgumentType.columnFinePosition(argument.isCenterIntegers()),
                (argument, source, resolver) -> {
                    WrappedColumnFinePosition position = resolver.resolve(source);
                    return new Location(source.getLocation().getWorld(), position.getX(), 0, position.getZ());
                }
        ));

        register(ColumnBlockPositionArgument.class, ArgumentMapper.constant(
                MinecraftArgumentType.columnBlockPosition(),
                (argument, source, resolver) -> {
                    WrappedColumnBlockPosition position = resolver.resolve(source);
                    return new Location(source.getLocation().getWorld(), position.getBlockX(), 0, position.getBlockZ());
                }
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