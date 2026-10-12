package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.arguments.ArgumentType;
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
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftArgumentProvider;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftProviderLoader;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Set;
import java.util.UUID;

public final class MinecraftArgumentType {

    private MinecraftArgumentType() {
    }

    private static final class Holder {

        private static final MinecraftArgumentProvider INSTANCE = MinecraftProviderLoader.argumentProvider(JavaPlugin.getProvidingPlugin(MinecraftArgumentType.class))
                .orElseThrow(() -> new IllegalStateException("No MinecraftArgumentProvider is available for this server version"));

    }

    private static MinecraftArgumentProvider provider() {
        return Holder.INSTANCE;
    }

    public static ArgumentType<EntitySelectorArgumentResolver> entity() {
        return provider().entity();
    }

    public static ArgumentType<EntitySelectorArgumentResolver> entities() {
        return provider().entities();
    }

    public static ArgumentType<PlayerSelectorArgumentResolver> player() {
        return provider().player();
    }

    public static ArgumentType<PlayerSelectorArgumentResolver> players() {
        return provider().players();
    }

    public static ArgumentType<PlayerProfileListResolver> playerProfiles() {
        return provider().playerProfiles();
    }

    public static ArgumentType<BlockPositionResolver> blockPosition() {
        return provider().blockPosition();
    }

    public static ArgumentType<ColumnBlockPositionResolver> columnBlockPosition() {
        return provider().columnBlockPosition();
    }

    public static ArgumentType<BlockInWorldPredicate> blockInWorldPredicate() {
        return provider().blockInWorldPredicate();
    }

    public static ArgumentType<FinePositionResolver> finePosition() {
        return provider().finePosition(true);
    }

    public static ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers) {
        return provider().finePosition(centerIntegers);
    }

    public static ArgumentType<ColumnFinePositionResolver> columnFinePosition() {
        return provider().columnFinePosition(true);
    }

    public static ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers) {
        return provider().columnFinePosition(centerIntegers);
    }

    public static ArgumentType<RotationResolver> rotation() {
        return provider().rotation();
    }

    public static ArgumentType<AngleResolver> angle() {
        return provider().angle();
    }

    public static ArgumentType<Set<Axis>> axes() {
        return provider().axes();
    }

    public static ArgumentType<BlockState> blockState() {
        return provider().blockState();
    }

    public static ArgumentType<ItemStack> itemStack() {
        return provider().itemStack();
    }

    public static ArgumentType<ItemStackPredicate> itemStackPredicate() {
        return provider().itemStackPredicate();
    }

    public static ArgumentType<Color> color() {
        return provider().color();
    }

    public static ArgumentType<Color> hexColor() {
        return provider().hexColor();
    }

    public static ArgumentType<String> message() {
        return provider().message();
    }

    public static ArgumentType<DisplaySlot> scoreboardDisplaySlot() {
        return provider().scoreboardDisplaySlot();
    }

    public static ArgumentType<NamespacedKey> namespacedKey() {
        return provider().namespacedKey();
    }

    public static ArgumentType<RangedNumber<Integer>> integerRange() {
        return provider().integerRange();
    }

    public static ArgumentType<RangedNumber<Double>> doubleRange() {
        return provider().doubleRange();
    }

    public static ArgumentType<RangedNumber<Float>> floatRange() {
        return provider().floatRange();
    }

    public static ArgumentType<World> world() {
        return provider().world();
    }

    public static ArgumentType<GameMode> gameMode() {
        return provider().gameMode();
    }

    public static ArgumentType<Particle> particle() {
        return provider().particle();
    }

    public static ArgumentType<UUID> uuid() {
        return provider().uuid();
    }

    public static ArgumentType<Integer> time() {
        return provider().time(0);
    }

    public static ArgumentType<Integer> time(int minTicks) {
        return provider().time(minTicks);
    }

    public static ArgumentType<Mirror> templateMirror() {
        return provider().templateMirror();
    }

    public static ArgumentType<StructureRotation> templateRotation() {
        return provider().templateRotation();
    }

}