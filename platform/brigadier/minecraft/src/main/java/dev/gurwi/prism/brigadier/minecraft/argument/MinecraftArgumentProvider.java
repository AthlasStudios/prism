package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.BlockInWorldPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.predicate.ItemStackPredicate;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.*;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftCommandDispatcherProvider;
import dev.gurwi.prism.bukkit.argument.type.RangedNumber;
import dev.gurwi.prism.reflection.ReflectionUtil;
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

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MinecraftArgumentProvider {

    static @NotNull Optional<MinecraftArgumentProvider> get() {
        String name = MinecraftArgumentProvider.class.getPackageName();
        String basePackage = name.substring(0, name.lastIndexOf('.')) + ".";

        String candidate = MinecraftCommandDispatcherProvider.hasPaperBrigadierApi() ? "paper" : "V1_21";

        try {
            Class<? extends MinecraftArgumentProvider> provider = Class.forName(basePackage + candidate + ".ArgumentProvider", true, MinecraftArgumentProvider.class.getClassLoader())
                    .asSubclass(MinecraftArgumentProvider.class);

            return Optional.of(ReflectionUtil.instantiate(provider));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    ArgumentType<EntitySelectorArgumentResolver> entity();

    ArgumentType<EntitySelectorArgumentResolver> entities();

    ArgumentType<PlayerSelectorArgumentResolver> player();

    ArgumentType<PlayerSelectorArgumentResolver> players();

    ArgumentType<PlayerProfileListResolver> playerProfiles();

    ArgumentType<BlockPositionResolver> blockPosition();

    ArgumentType<ColumnBlockPositionResolver> columnBlockPosition();

    ArgumentType<BlockInWorldPredicate> blockInWorldPredicate();

    ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers);

    ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers);

    ArgumentType<RotationResolver> rotation();

    ArgumentType<AngleResolver> angle();

    ArgumentType<Set<Axis>> axes();

    ArgumentType<BlockState> blockState();

    ArgumentType<ItemStack> itemStack();

    ArgumentType<ItemStackPredicate> itemStackPredicate();

    ArgumentType<Color> color();

    ArgumentType<Color> hexColor();

    ArgumentType<String> message();

    ArgumentType<DisplaySlot> scoreboardDisplaySlot();

    ArgumentType<NamespacedKey> namespacedKey();

    ArgumentType<RangedNumber<Integer>> integerRange();

    ArgumentType<RangedNumber<Double>> doubleRange();

    ArgumentType<RangedNumber<Float>> floatRange();

    ArgumentType<World> world();

    ArgumentType<GameMode> gameMode();

//    ArgumentType<HeightMap> heightMap();

    ArgumentType<UUID> uuid();

//    ArgumentType<Criteria> criteria();
//
//    ArgumentType<LookAnchor> lookAnchor();

    ArgumentType<Integer> time(int minTicks);

    ArgumentType<Mirror> templateMirror();

    ArgumentType<StructureRotation> templateRotation();

}
