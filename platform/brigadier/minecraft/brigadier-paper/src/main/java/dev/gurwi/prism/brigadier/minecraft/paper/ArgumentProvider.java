package dev.gurwi.prism.brigadier.minecraft.paper;

import com.google.common.collect.Range;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.gurwi.prism.brigadier.minecraft.argument.MinecraftArgumentProvider;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.BlockInWorldPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.ItemStackPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.AngleResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.BlockPositionResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.ColumnBlockPositionResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.ColumnFinePositionResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.EntitySelectorArgumentResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.FinePositionResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.PlayerProfileListResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.PlayerSelectorArgumentResolver;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.RotationResolver;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedBlockPosition;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedColumnBlockPosition;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedColumnFinePosition;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedPosition;
import dev.gurwi.prism.brigadier.minecraft.wrapper.position.WrappedRotation;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.range.RangeProvider;
import org.bukkit.Axis;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ArgumentProvider implements MinecraftArgumentProvider {

    private static final SimpleCommandExceptionType UNSUPPORTED_SOURCE = new SimpleCommandExceptionType(new LiteralMessage("This argument cannot be resolved for this sender"));

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entity() {
        return PaperBackedArgumentType.of(ArgumentTypes.entity(), paper -> source -> paper.resolve(stack(source)));
    }

    @Override
    public ArgumentType<EntitySelectorArgumentResolver> entities() {
        return PaperBackedArgumentType.of(ArgumentTypes.entities(), paper -> source -> paper.resolve(stack(source)));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> player() {
        return PaperBackedArgumentType.of(ArgumentTypes.player(), paper -> source -> paper.resolve(stack(source)));
    }

    @Override
    public ArgumentType<PlayerSelectorArgumentResolver> players() {
        return PaperBackedArgumentType.of(ArgumentTypes.players(), paper -> source -> paper.resolve(stack(source)));
    }

    @Override
    public ArgumentType<PlayerProfileListResolver> playerProfiles() {
        return null;
    }

//    @Override
//    public ArgumentType<PlayerProfileListResolver> playerProfiles() {
//        return PaperBackedArgumentType.of(ArgumentTypes.playerProfiles(), paper -> source ->
//                Collections.unmodifiableCollection(paper.resolve(stack(source)))
//        );
//    }

    @Override
    public ArgumentType<BlockPositionResolver> blockPosition() {
        return PaperBackedArgumentType.of(ArgumentTypes.blockPosition(), paper -> source -> {
            var position = paper.resolve(stack(source));
            return new WrappedBlockPosition(position.blockX(), position.blockY(), position.blockZ());
        });
    }

    @Override
    public ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        return null;
    }

    @Override
    public ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        return null;
    }

    @Override
    public ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        return null;
    }

    @Override
    public ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        return null;
    }

    @Override
    public ArgumentType<RotationResolver> rotation() {
        return null;
    }

    @Override
    public ArgumentType<AngleResolver> angle() {
        return null;
    }

    @Override
    public ArgumentType<Set<Axis>> axes() {
        return null;
    }

//    @Override
//    public ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
//        return PaperBackedArgumentType.of(ArgumentTypes.columnBlockPosition(), paper -> source -> {
//            var position = paper.resolve(stack(source));
//            return new WrappedColumnBlockPosition(position.blockX(), position.blockZ());
//        });
//    }
//
//    @Override
//    public ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
//        return PaperBackedArgumentType.of(ArgumentTypes.blockInWorldPredicate(), paper -> (block, loadChunk) ->
//                BlockInWorldPredicate.Result.valueOf(paper.testBlock(block, loadChunk).name())
//        );
//    }

//    @Override
//    public ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
//        return PaperBackedArgumentType.of(ArgumentTypes.finePosition(centerIntegers), paper -> source -> {
//            var position = paper.resolve(stack(source));
//            return WrappedPosition.fine(position.x(), position.y(), position.z());
//        });
//    }

//    @Override
//    public ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
//        return PaperBackedArgumentType.of(ArgumentTypes.columnFinePosition(centerIntegers), paper -> source -> {
//            var position = paper.resolve(stack(source));
//            return new WrappedColumnFinePosition(position.x(), position.z());
//        });
//    }

//    @Override
//    public ArgumentType<RotationResolver> rotation() {
//        return PaperBackedArgumentType.of(ArgumentTypes.rotation(), paper -> source -> {
//            var rotation = paper.resolve(stack(source));
//            return new WrappedRotation(rotation.yaw(), rotation.pitch());
//        });
//    }
//
//    @Override
//    public ArgumentType<AngleResolver> angle() {
//        return PaperBackedArgumentType.of(ArgumentTypes.angle(), paper -> source -> paper.resolve(stack(source)));
//    }
//
//    @Override
//    public ArgumentType<Set<Axis>> axes() {
//        return PaperBackedArgumentType.of(ArgumentTypes.axes(), axes -> axes);
//    }

    @Override
    public ArgumentType<BlockState> blockState() {
        return ArgumentTypes.blockState();
    }

    @Override
    public ArgumentType<ItemStack> itemStack() {
        return ArgumentTypes.itemStack();
    }

    @Override
    public ArgumentType<ItemStackPredicate> itemStackPredicate() {
        return PaperBackedArgumentType.of(ArgumentTypes.itemPredicate(), paper -> paper::test);
    }

    @Override
    public ArgumentType<Color> color() {
        return PaperBackedArgumentType.of(ArgumentTypes.namedColor(), color -> new Color(color.value()));
    }

    @Override
    public ArgumentType<Color> hexColor() {
        return null;
    }

//    @Override
//    public ArgumentType<Color> hexColor() {
//        return PaperBackedArgumentType.of(ArgumentTypes.hexColor(), color -> new Color(color.value()));
//    }

    @Override
    public ArgumentType<String> message() {
        return StringArgumentType.greedyString();
    }

    @Override
    public ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        return ArgumentTypes.scoreboardDisplaySlot();
    }

    @Override
    public ArgumentType<NamespacedKey> namespacedKey() {
        return ArgumentTypes.namespacedKey();
    }

    @Override
    public ArgumentType<RangedNumber<Integer>> integerRange() {
        return range(ArgumentTypes.integerRange(), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public ArgumentType<RangedNumber<Double>> doubleRange() {
        return range(ArgumentTypes.doubleRange(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public ArgumentType<RangedNumber<Float>> floatRange() {
        return PaperBackedArgumentType.of(ArgumentTypes.doubleRange(), provider -> {
            Range<Double> range = provider.range();

            return new RangedNumber<>(
                    range.hasLowerBound() ? range.lowerEndpoint().floatValue() : Float.NEGATIVE_INFINITY,
                    range.hasUpperBound() ? range.upperEndpoint().floatValue() : Float.POSITIVE_INFINITY
            );
        });
    }

    @Override
    public ArgumentType<World> world() {
        return ArgumentTypes.world();
    }

    @Override
    public ArgumentType<GameMode> gameMode() {
        return ArgumentTypes.gameMode();
    }

    @Override
    public ArgumentType<UUID> uuid() {
        return ArgumentTypes.uuid();
    }

    @Override
    public ArgumentType<Integer> time(int minTicks) {
        return ArgumentTypes.time(minTicks);
    }

    @Override
    public ArgumentType<Mirror> templateMirror() {
        return ArgumentTypes.templateMirror();
    }

    @Override
    public ArgumentType<StructureRotation> templateRotation() {
        return ArgumentTypes.templateRotation();
    }

    private <N extends Number & Comparable<N>> @NotNull ArgumentType<RangedNumber<N>> range(ArgumentType<? extends RangeProvider<N>> nativeType, N lowest, N highest) {
        return PaperBackedArgumentType.of(nativeType, provider -> {
            Range<N> range = provider.range();

            return new RangedNumber<>(
                    range.hasLowerBound() ? range.lowerEndpoint() : lowest,
                    range.hasUpperBound() ? range.upperEndpoint() : highest
            );
        });
    }

    private static CommandSourceStack stack(MinecraftCommandSource source) throws CommandSyntaxException {
        if (source instanceof WrappedSourceStack wrapped) {
            return wrapped.getHandle();
        }

        throw UNSUPPORTED_SOURCE.create();
    }

}