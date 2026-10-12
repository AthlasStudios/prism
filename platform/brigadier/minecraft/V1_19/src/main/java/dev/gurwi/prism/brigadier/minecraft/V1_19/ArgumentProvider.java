package dev.gurwi.prism.brigadier.minecraft.V1_19;

import com.google.common.collect.Collections2;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftArgumentProvider;
import dev.gurwi.prism.brigadier.minecraft.argument.NativeBackedArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.BlockInWorldPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.ItemStackPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.*;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.WrappedGameProfile;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.*;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.v1_19_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlockStates;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

public class ArgumentProvider implements MinecraftArgumentProvider {

    private static final Map<String, DisplaySlot> DISPLAY_SLOTS = Map.ofEntries(
            Map.entry("belowName", DisplaySlot.BELOW_NAME),
            Map.entry("list", DisplaySlot.PLAYER_LIST),
            Map.entry("sidebar", DisplaySlot.SIDEBAR),
            Map.entry("sidebar.team.black", DisplaySlot.SIDEBAR_BLACK),
            Map.entry("sidebar.team.dark_blue", DisplaySlot.SIDEBAR_DARK_BLUE),
            Map.entry("sidebar.team.dark_green", DisplaySlot.SIDEBAR_DARK_GREEN),
            Map.entry("sidebar.team.dark_aqua", DisplaySlot.SIDEBAR_DARK_AQUA),
            Map.entry("sidebar.team.dark_red", DisplaySlot.SIDEBAR_DARK_RED),
            Map.entry("sidebar.team.dark_purple", DisplaySlot.SIDEBAR_DARK_PURPLE),
            Map.entry("sidebar.team.gold", DisplaySlot.SIDEBAR_GOLD),
            Map.entry("sidebar.team.gray", DisplaySlot.SIDEBAR_GRAY),
            Map.entry("sidebar.team.dark_gray", DisplaySlot.SIDEBAR_DARK_GRAY),
            Map.entry("sidebar.team.blue", DisplaySlot.SIDEBAR_BLUE),
            Map.entry("sidebar.team.green", DisplaySlot.SIDEBAR_GREEN),
            Map.entry("sidebar.team.aqua", DisplaySlot.SIDEBAR_AQUA),
            Map.entry("sidebar.team.red", DisplaySlot.SIDEBAR_RED),
            Map.entry("sidebar.team.light_purple", DisplaySlot.SIDEBAR_LIGHT_PURPLE),
            Map.entry("sidebar.team.yellow", DisplaySlot.SIDEBAR_YELLOW),
            Map.entry("sidebar.team.white", DisplaySlot.SIDEBAR_WHITE)
    );

    private final CommandBuildContext context = CommandBuildContext.simple(MinecraftServer.getServer().registryAccess(), MinecraftServer.getServer().overworld().enabledFeatures());

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entity() {
        return NativeBackedArgumentType.of(EntityArgument.entity(), selector -> source -> List.of(
                selector.findSingleEntity(stack(source)).getBukkitEntity()
        ));
    }

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entities() {
        return NativeBackedArgumentType.of(EntityArgument.entities(), selector -> source -> entities(selector, source));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> player() {
        return NativeBackedArgumentType.of(EntityArgument.player(), selector -> source -> List.of(selector.findSinglePlayer(stack(source)).getBukkitEntity()));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> players() {
        return NativeBackedArgumentType.of(EntityArgument.players(), selector -> source -> players(selector, source));
    }

    @Override
    public ArgumentType<PlayerProfileListResolver> playerProfiles() {
        return NativeBackedArgumentType.of(
                GameProfileArgument.gameProfile(),
                result -> source -> Collections.unmodifiableCollection(
                        Collections2.transform(
                                result.getNames(stack(source)),
                                profile -> new WrappedGameProfile(profile)
                        )
                )
        );
    }

    @Override
    public ArgumentType<BlockPositionResolver> blockPosition() {
        return NativeBackedArgumentType.of(BlockPosArgument.blockPos(), result -> source -> {
            BlockPos pos = result.getBlockPos(stack(source));
            return new WrappedBlockPosition(pos.getX(), pos.getY(), pos.getZ());
        });
    }

    @Override
    public ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        return NativeBackedArgumentType.of(ColumnPosArgument.columnPos(), result -> source -> {
            BlockPos pos = result.getBlockPos(stack(source));
            return new WrappedColumnBlockPosition(pos.getX(), pos.getZ());
        });
    }

    @Override
    public ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        return NativeBackedArgumentType.of(BlockPredicateArgument.blockPredicate(context), result -> (block, loadChunk) -> {
            BlockInWorld blockInWorld = new BlockInWorld(
                    ((CraftWorld) block.getWorld()).getHandle(),
                    new BlockPos(block.getX(), block.getY(), block.getZ()),
                    loadChunk
            );

            if (blockInWorld.getState() == null) return BlockInWorldPredicate.Result.UNLOADED_CHUNK;
            return result.test(blockInWorld) ? BlockInWorldPredicate.Result.TRUE : BlockInWorldPredicate.Result.FALSE;
        });
    }

    @Override
    public ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        return NativeBackedArgumentType.of(Vec3Argument.vec3(centerIntegers), result -> source -> {
            Vec3 vec3 = result.getPosition(stack(source));
            return WrappedPosition.fine(vec3.x, vec3.y, vec3.z);
        });
    }

    @Override
    public ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        return NativeBackedArgumentType.of(Vec2Argument.vec2(centerIntegers), result -> source -> {
            Vec3 vec3 = result.getPosition(stack(source));
            return new WrappedColumnFinePosition(vec3.x, vec3.z);
        });
    }

    @Override
    public ArgumentType<RotationResolver> rotation() {
        return NativeBackedArgumentType.of(RotationArgument.rotation(), result -> source -> {
            Vec2 vec2 = result.getRotation(stack(source));
            return new WrappedRotation(vec2.y, vec2.x);
        });
    }

    @Override
    public ArgumentType<AngleResolver> angle() {
        return NativeBackedArgumentType.of(AngleArgument.angle(), result -> source -> result.getAngle(stack(source)));
    }

    @Override
    public ArgumentType<Set<Axis>> axes() {
        return NativeBackedArgumentType.of(SwizzleArgument.swizzle(), result -> {
            EnumSet<Axis> bukkitAxes = EnumSet.noneOf(Axis.class);

            for (Direction.Axis nmsAxis : result) {
                bukkitAxes.add(Axis.valueOf(nmsAxis.name()));
            }

            return bukkitAxes;
        });
    }

    @Override
    public ArgumentType<BlockState> blockState() {
        return NativeBackedArgumentType.of(BlockStateArgument.block(context), result ->
                CraftBlockStates.getBlockState(BlockPos.ZERO, result.getState(), null)
        );
    }

    @Override
    public ArgumentType<ItemStack> itemStack() {
        return NativeBackedArgumentType.of(ItemArgument.item(context), result -> {
            try {
                return CraftItemStack.asBukkitCopy(result.createItemStack(1, true));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ArgumentType<ItemStackPredicate> itemStackPredicate() {
        return NativeBackedArgumentType.of(ItemPredicateArgument.itemPredicate(context), result -> item -> result.test(CraftItemStack.asNMSCopy(item)));
    }

    @Override
    public ArgumentType<Color> color() {
        return NativeBackedArgumentType.of(ColorArgument.color(), result -> {
            Integer rgb = result.getColor();
            return rgb == null ? Color.WHITE : Color.fromRGB(rgb);
        });
    }

    // TODO -> COMPONENT ARGUMENT
    // TODO -> STYLE ARGUMENT

    @Override
    public ArgumentType<String> message() {
        return NativeBackedArgumentType.of(MessageArgument.message(), MessageArgument.Message::getText);
    }

    @Override
    public ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        return NativeBackedArgumentType.of(ScoreboardSlotArgument.displaySlot(), slot -> {
            DisplaySlot bukkit = DISPLAY_SLOTS.get(Scoreboard.getDisplaySlotName(slot));

            if (bukkit == null) {
                throw UNKNOWN_SLOT.create(slot);
            }

            return bukkit;
        });
    }

    @Override
    public ArgumentType<NamespacedKey> namespacedKey() {
        return NativeBackedArgumentType.of(ResourceLocationArgument.id(), CraftNamespacedKey::fromMinecraft);
    }

    @Override
    public ArgumentType<RangedNumber<Integer>> integerRange() {
        return range(RangeArgument.intRange(), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public ArgumentType<RangedNumber<Double>> doubleRange() {
        return range(RangeArgument.floatRange(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public ArgumentType<RangedNumber<Float>> floatRange() {
        return NativeBackedArgumentType.of(RangeArgument.floatRange(), bounds -> new RangedNumber<>(
                bounds.getMin() == null ? Float.NEGATIVE_INFINITY : bounds.getMin().floatValue(),
                bounds.getMax() == null ? Float.POSITIVE_INFINITY : bounds.getMax().floatValue()
        ));
    }

    @Override
    public ArgumentType<World> world() {
        return NativeBackedArgumentType.of(DimensionArgument.dimension(), worldId -> {
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, worldId);
            ServerLevel level = MinecraftServer.getServer().getLevel(key);

            if (level == null) {
                throw UNKNOWN_DIMENSION.create(worldId);
            }

            return level.getWorld();
        });
    }

    @Override
    public ArgumentType<GameMode> gameMode() {
        return NativeBackedArgumentType.of(GameModeArgument.gameMode(), gameMode -> switch (gameMode.getId()) {
            case 0 -> GameMode.SURVIVAL;
            case 1 -> GameMode.CREATIVE;
            case 2 -> GameMode.ADVENTURE;
            case 3 -> GameMode.SPECTATOR;
            default -> null;
        });
    }

    @Override
    public ArgumentType<Particle> particle() {
        return NativeBackedArgumentType.of(ParticleArgument.particle(context), particle -> CraftParticle.toBukkit(particle.getType()));
    }

    @Override
    public ArgumentType<UUID> uuid() {
        return NativeBackedArgumentType.of(UuidArgument.uuid(), uuid -> uuid);
    }

    @Override
    public ArgumentType<Integer> time(int minTicks) {
        return NativeBackedArgumentType.of(TimeArgument.time(), ticks -> {
            if (ticks < minTicks) {
                throw TIME_TOO_SMALL.create(ticks, minTicks);
            }

            return ticks;
        });
    }

    @Override
    public ArgumentType<Mirror> templateMirror() {
        return NativeBackedArgumentType.of(TemplateMirrorArgument.templateMirror(), mirror -> Mirror.valueOf(mirror.name()));
    }

    @Override
    public ArgumentType<StructureRotation> templateRotation() {
        return NativeBackedArgumentType.of(TemplateRotationArgument.templateRotation(), rotation -> StructureRotation.valueOf(rotation.name()));
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    private <N extends Number & Comparable<N>> @NotNull ArgumentType<RangedNumber<N>> range(ArgumentType<? extends MinMaxBounds<N>> nativeType, N lowest, N highest) {
        return NativeBackedArgumentType.of(nativeType, bounds -> new RangedNumber<>(
                bounds.getMin() == null ? lowest : bounds.getMin(),
                bounds.getMax() == null ? highest : bounds.getMax()
        ));
    }

    private @NotNull List<Entity> entities(@NotNull EntitySelector selector, MinecraftCommandSource source) throws CommandSyntaxException {
        List<Entity> result = new ArrayList<>();

        for (net.minecraft.world.entity.Entity entity : selector.findEntities(stack(source))) {
            result.add(entity.getBukkitEntity());
        }

        return result;
    }

    private @NotNull List<Player> players(@NotNull EntitySelector selector, MinecraftCommandSource source) throws CommandSyntaxException {
        List<Player> result = new ArrayList<>();

        for (ServerPlayer player : selector.findPlayers(stack(source))) {
            result.add(player.getBukkitEntity());
        }

        return result;
    }

    private CommandSourceStack stack(MinecraftCommandSource source) throws CommandSyntaxException {
        if (source instanceof WrappedSourceStack wrapped) {
            return wrapped.getHandle();
        }

        throw UNSUPPORTED_SOURCE.create();
    }

}