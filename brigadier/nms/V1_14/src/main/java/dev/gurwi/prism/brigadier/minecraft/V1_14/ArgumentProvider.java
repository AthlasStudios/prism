package dev.gurwi.prism.brigadier.minecraft.V1_14;

import com.google.common.collect.Collections2;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.argument.NativeBackedArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.BlockInWorldPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.ItemStackPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.*;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftArgumentProvider;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.WrappedGameProfile;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.*;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import net.minecraft.server.v1_14_R1.ArgumentBlockPredicate;
import net.minecraft.server.v1_14_R1.ArgumentChat;
import net.minecraft.server.v1_14_R1.ArgumentChatFormat;
import net.minecraft.server.v1_14_R1.ArgumentCriterionValue;
import net.minecraft.server.v1_14_R1.ArgumentDimension;
import net.minecraft.server.v1_14_R1.ArgumentEntity;
import net.minecraft.server.v1_14_R1.ArgumentItemPredicate;
import net.minecraft.server.v1_14_R1.ArgumentItemStack;
import net.minecraft.server.v1_14_R1.ArgumentMinecraftKeyRegistered;
import net.minecraft.server.v1_14_R1.ArgumentParticle;
import net.minecraft.server.v1_14_R1.ArgumentPosition;
import net.minecraft.server.v1_14_R1.ArgumentProfile;
import net.minecraft.server.v1_14_R1.ArgumentRotation;
import net.minecraft.server.v1_14_R1.ArgumentScoreboardSlot;
import net.minecraft.server.v1_14_R1.ArgumentTile;
import net.minecraft.server.v1_14_R1.ArgumentTime;
import net.minecraft.server.v1_14_R1.ArgumentVec2;
import net.minecraft.server.v1_14_R1.ArgumentVec2I;
import net.minecraft.server.v1_14_R1.ArgumentVec3;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.CommandListenerWrapper;
import net.minecraft.server.v1_14_R1.CriterionConditionValue;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySelector;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.ShapeDetectorBlock;
import net.minecraft.server.v1_14_R1.Vec2F;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_14_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArgumentProvider implements MinecraftArgumentProvider {

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entity() {
        return NativeBackedArgumentType.of(ArgumentEntity.a(), selector -> source -> List.of(
                selector.a(stack(source)).getBukkitEntity()
        ));
    }

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entities() {
        return NativeBackedArgumentType.of(ArgumentEntity.multipleEntities(), selector -> source -> entities(selector, source));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> player() {
        return NativeBackedArgumentType.of(ArgumentEntity.c(), selector -> source -> List.of(
                selector.c(stack(source)).getBukkitEntity()
        ));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> players() {
        return NativeBackedArgumentType.of(ArgumentEntity.d(), selector -> source -> players(selector, source));
    }

    @Override
    public ArgumentType<PlayerProfileListResolver> playerProfiles() {
        return NativeBackedArgumentType.of(
                ArgumentProfile.a(),
                result -> source -> Collections.unmodifiableCollection(
                        Collections2.transform(result.getNames(stack(source)), WrappedGameProfile::new)
                )
        );
    }

    @Override
    public ArgumentType<BlockPositionResolver> blockPosition() {
        return NativeBackedArgumentType.of(ArgumentPosition.a(), result -> source -> {
            BlockPosition pos = result.c(stack(source));
            return new WrappedBlockPosition(pos.getX(), pos.getY(), pos.getZ());
        });
    }

    @Override
    public ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        return NativeBackedArgumentType.of(ArgumentVec2I.a(), result -> source -> {
            BlockPosition pos = result.c(stack(source));
            return new WrappedColumnBlockPosition(pos.getX(), pos.getZ());
        });
    }

    @Override
    public ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        return NativeBackedArgumentType.of(ArgumentBlockPredicate.a(), result -> {
            Predicate<ShapeDetectorBlock> predicate = result.create(MinecraftServer.getServer().getTagRegistry());

            return (block, loadChunk) -> {
                ShapeDetectorBlock shapeDetectorBlock = new ShapeDetectorBlock(
                        ((CraftWorld) block.getWorld()).getHandle(),
                        new BlockPosition(block.getX(), block.getY(), block.getZ()),
                        loadChunk
                );

                if (shapeDetectorBlock.a() == null) {
                    return BlockInWorldPredicate.Result.UNLOADED_CHUNK;
                }

                return predicate.test(shapeDetectorBlock) ? BlockInWorldPredicate.Result.TRUE : BlockInWorldPredicate.Result.FALSE;
            };
        });
    }

    @Override
    public ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        return NativeBackedArgumentType.of(ArgumentVec3.a(centerIntegers), result -> source -> {
            Vec3D vec3 = result.a(stack(source));
            return WrappedPosition.fine(vec3.x, vec3.y, vec3.z);
        });
    }

    @Override
    public ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        return NativeBackedArgumentType.of(ArgumentVec2.a(), result -> source -> {
            Vec3D vec3 = result.a(stack(source));
            return new WrappedColumnFinePosition(vec3.x, vec3.z);
        });
    }

    @Override
    public ArgumentType<RotationResolver> rotation() {
        return NativeBackedArgumentType.of(ArgumentRotation.a(), result -> source -> {
            Vec2F vec2 = result.b(stack(source));
            return new WrappedRotation(vec2.j, vec2.i);
        });
    }

    @Override
    public ArgumentType<BlockState> blockState() {
        return NativeBackedArgumentType.of(ArgumentTile.a(), result -> {
            World world = Bukkit.getWorlds().get(0);
            BlockState state = world.getBlockAt(0, 0, 0).getState();
            state.setBlockData(CraftBlockData.fromData(result.a()));
            return state;
        });
    }

    @Override
    public ArgumentType<ItemStack> itemStack() {
        return NativeBackedArgumentType.of(ArgumentItemStack.a(), result ->
                CraftItemStack.asBukkitCopy(result.a(1, true))
        );
    }

    @Override
    public ArgumentType<ItemStackPredicate> itemStackPredicate() {
        return NativeBackedArgumentType.of(ArgumentItemPredicate.a(), result -> {
            Predicate<net.minecraft.server.v1_14_R1.ItemStack> predicate = result.create(serverContext());

            return item -> predicate.test(CraftItemStack.asNMSCopy(item));
        });
    }

    @Override
    public ArgumentType<ChatColor> color() {
        return NativeBackedArgumentType.of(ArgumentChatFormat.a(), result -> ChatColor.valueOf(result.name().toUpperCase()));
    }

    @Override
    public ArgumentType<String> message() {
        return NativeBackedArgumentType.of(ArgumentChat.a(), message ->
                message.a(MinecraftServer.getServer().getServerCommandListener(), false).getString()
        );
    }

    @Override
    public ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        return NativeBackedArgumentType.of(ArgumentScoreboardSlot.a(), slot -> switch (slot) {
            case 0 -> DisplaySlot.PLAYER_LIST;
            case 1 -> DisplaySlot.SIDEBAR;
            case 2 -> DisplaySlot.BELOW_NAME;
            default -> throw UNKNOWN_SLOT.create(slot);
        });
    }

    @Override
    public ArgumentType<NamespacedKey> namespacedKey() {
        return NativeBackedArgumentType.of(ArgumentMinecraftKeyRegistered.a(), CraftNamespacedKey::fromMinecraft);
    }

    @Override
    public ArgumentType<RangedNumber<Integer>> integerRange() {
        return range(ArgumentCriterionValue.a(), Integer.MIN_VALUE, Integer.MAX_VALUE, Number::intValue);
    }

    @Override
    public ArgumentType<RangedNumber<Double>> doubleRange() {
        return range(ArgumentCriterionValue.a(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Number::doubleValue);
    }

    @Override
    public ArgumentType<RangedNumber<Float>> floatRange() {
        return range(ArgumentCriterionValue.a(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Number::floatValue);
    }

    @Override
    public ArgumentType<World> world() {
        return NativeBackedArgumentType.of(ArgumentDimension.a(), dimension -> {
            WorldServer level = MinecraftServer.getServer().getWorldServer(dimension);

            if (level == null) {
                throw UNKNOWN_DIMENSION.create(dimension);
            }

            return level.getWorld();
        });
    }

    @Override
    public ArgumentType<Particle> particle() {
        return NativeBackedArgumentType.of(ArgumentParticle.a(), CraftParticle::toBukkit);
    }

    @Override
    public ArgumentType<Integer> time(int minTicks) {
        return NativeBackedArgumentType.of(ArgumentTime.a(), ticks -> {
            if (ticks < minTicks) {
                throw TIME_TOO_SMALL.create(ticks, minTicks);
            }

            return ticks;
        });
    }

    private <S extends Number, N extends Number & Comparable<N>> @NotNull ArgumentType<RangedNumber<N>> range(
            ArgumentType<? extends CriterionConditionValue<S>> nativeType,
            N lowest,
            N highest,
            Function<Number, N> converter
    ) {
        return NativeBackedArgumentType.of(nativeType, bounds -> new RangedNumber<>(
                bounds.a() == null ? lowest : converter.apply(bounds.a()),
                bounds.b() == null ? highest : converter.apply(bounds.b())
        ));
    }

    private static @NotNull CommandContext<CommandListenerWrapper> serverContext() {
        return new CommandContext<>(
                MinecraftServer.getServer().getServerCommandListener(),
                "",
                new HashMap<>(),
                null,
                null,
                List.of(),
                StringRange.at(0),
                null,
                null,
                false
        );
    }

    private static @NotNull List<Entity> entities(@NotNull EntitySelector selector, MinecraftCommandSource source) throws CommandSyntaxException {
        List<Entity> result = new ArrayList<>();

        for (net.minecraft.server.v1_14_R1.Entity entity : selector.getEntities(stack(source))) {
            result.add(entity.getBukkitEntity());
        }

        return result;
    }

    private static @NotNull List<Player> players(@NotNull EntitySelector selector, MinecraftCommandSource source) throws CommandSyntaxException {
        List<Player> result = new ArrayList<>();

        for (EntityPlayer player : selector.d(stack(source))) {
            result.add(player.getBukkitEntity());
        }

        return result;
    }

    private static CommandListenerWrapper stack(MinecraftCommandSource source) throws CommandSyntaxException {
        if (source instanceof WrappedSourceStack wrapped) {
            return wrapped.getHandle();
        }

        throw UNSUPPORTED_SOURCE.create();
    }

}