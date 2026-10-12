package dev.gurwi.prism.brigadier.minecraft.provider;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.BlockInWorldPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.ItemStackPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.*;
import dev.gurwi.prism.brigadier.minecraft.exception.UnsupportedArgumentTypeException;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface MinecraftArgumentProvider {

    SimpleCommandExceptionType UNSUPPORTED_SOURCE = new SimpleCommandExceptionType(new LiteralMessage("This argument cannot be resolved for this sender"));
    DynamicCommandExceptionType UNKNOWN_DIMENSION = new DynamicCommandExceptionType(id -> new LiteralMessage("Unknown dimension '" + id + "'"));
    DynamicCommandExceptionType UNKNOWN_SLOT = new DynamicCommandExceptionType(slot -> new LiteralMessage("Unsupported display slot " + slot));
    Dynamic2CommandExceptionType TIME_TOO_SMALL = new Dynamic2CommandExceptionType((found, minimum) -> new LiteralMessage("Duration must not be less than " + minimum + " ticks, found " + found));

    @Contract("_ -> new")
    private static @NotNull UnsupportedArgumentTypeException unsupported(String argument) {
        return new UnsupportedArgumentTypeException(argument);
    }

    default ArgumentType<EntitySelectorArgumentResolver> entity() {
        throw unsupported("entity");
    }

    default ArgumentType<EntitySelectorArgumentResolver> entities() {
        throw unsupported("entities");
    }

    default ArgumentType<PlayerSelectorArgumentResolver> player() {
        throw unsupported("player");
    }

    default ArgumentType<PlayerSelectorArgumentResolver> players() {
        throw unsupported("players");
    }

    default ArgumentType<PlayerProfileListResolver> playerProfiles() {
        throw unsupported("playerProfiles");
    }

    default ArgumentType<BlockPositionResolver> blockPosition() {
        throw unsupported("blockPosition");
    }

    default ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        throw unsupported("columnBlockPosition");
    }

    default ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        throw unsupported("blockInWorldPredicate");
    }

    default ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        throw unsupported("finePosition");
    }

    default ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        throw unsupported("columnFinePosition");
    }

    default ArgumentType<RotationResolver> rotation() {
        throw unsupported("rotation");
    }

    default ArgumentType<AngleResolver> angle() {
        throw unsupported("angle");
    }

    default ArgumentType<Set<Axis>> axes() {
        throw unsupported("axes");
    }

    default ArgumentType<BlockState> blockState() {
        throw unsupported("blockState");
    }

    default ArgumentType<ItemStack> itemStack() {
        throw unsupported("itemStack");
    }

    default ArgumentType<ItemStackPredicate> itemStackPredicate() {
        throw unsupported("itemStackPredicate");
    }

    default ArgumentType<Color> color() {
        throw unsupported("color");
    }

    default ArgumentType<Color> hexColor() {
        throw unsupported("hexColor");
    }

    default ArgumentType<String> message() {
        throw unsupported("message");
    }

    default ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        throw unsupported("scoreboardDisplaySlot");
    }

    default ArgumentType<NamespacedKey> namespacedKey() {
        throw unsupported("namespacedKey");
    }

    default ArgumentType<RangedNumber<Integer>> integerRange() {
        throw unsupported("integerRange");
    }

    default ArgumentType<RangedNumber<Double>> doubleRange() {
        throw unsupported("doubleRange");
    }

    default ArgumentType<RangedNumber<Float>> floatRange() {
        throw unsupported("floatRange");
    }

    default ArgumentType<World> world() {
        throw unsupported("world");
    }

    default ArgumentType<GameMode> gameMode() {
        throw unsupported("gameMode");
    }

    default ArgumentType<Particle> particle() {
        throw unsupported("particle");
    }

//    ArgumentType<HeightMap> heightMap();

    default ArgumentType<UUID> uuid() {
        throw unsupported("uuid");
    }

//    ArgumentType<Criteria> criteria();
//
//    ArgumentType<LookAnchor> lookAnchor();

    default ArgumentType<Integer> time(int minTicks) {
        throw unsupported("time");
    }

    default ArgumentType<Mirror> templateMirror() {
        throw unsupported("templateMirror");
    }

    default ArgumentType<StructureRotation> templateRotation() {
        throw unsupported("templateRotation");
    }

}
