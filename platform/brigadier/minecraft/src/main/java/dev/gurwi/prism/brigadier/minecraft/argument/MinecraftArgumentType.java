package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.resolver.*;
import org.bukkit.Axis;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface MinecraftArgumentType {

    ArgumentType<EntitySelectorArgumentResolver> entity();

    ArgumentType<PlayerSelectorArgumentResolver> player();

    ArgumentType<EntitySelectorArgumentResolver> entities();

    ArgumentType<PlayerSelectorArgumentResolver> players();

    ArgumentType<PlayerProfileListResolver> playerProfiles();

    ArgumentType<BlockPositionResolver> blockPosition();

//    ArgumentType<ColumnBlockPositionResolver> columnBlockPosition();
//
//    ArgumentType<FinePositionResolver> finePosition(boolean centerIntegers);
//
//    ArgumentType<ColumnFinePositionResolver> columnFinePosition(boolean centerIntegers);
//
//    ArgumentType<RotationResolver> rotation();

    ArgumentType<AngleResolver> angle();

    ArgumentType<Set<Axis>> axes();

    ArgumentType<BlockState> blockState();

//    ArgumentType<BlockInWorldPredicate> blockInWorldPredicate();

    ArgumentType<ItemStack> itemStack();

    ArgumentType<Predicate<ItemStack>> itemStackPredicate();

//    ArgumentType<NamedTextColor> namedColor();
//
//    ArgumentType<TextColor> hexColor();
//
//    ArgumentType<Component> component();
//
//    ArgumentType<Style> style();
//
//    ArgumentType<SignedMessageResolver> signedMessage();

    ArgumentType<DisplaySlot> scoreboardDisplaySlot();

    ArgumentType<NamespacedKey> namespacedKey();

//    ArgumentType<Key> key();
//
//    ArgumentType<IntegerRangeProvider> integerRange();
//
//    ArgumentType<DoubleRangeProvider> doubleRange();

    ArgumentType<World> world();

    ArgumentType<GameMode> gameMode();

//    ArgumentType<HeightMap> heightMap();

    ArgumentType<UUID> uuid();

//    ArgumentType<Criteria> objectiveCriteria();
//
//    ArgumentType<LookAnchor> entityAnchor();

    ArgumentType<Integer> time(int minTicks);

    ArgumentType<Mirror> templateMirror();

    ArgumentType<StructureRotation> templateRotation();

//    <T> ArgumentType<TypedKey<T>> resourceKey(RegistryKey<T> registryKey);
//
//    <T> ArgumentType<T> resource(RegistryKey<T> registryKey);

}
