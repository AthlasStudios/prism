package dev.gurwi.prism.brigadier.minecraft.argument.predicate;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockInWorldPredicate {

    default Result testBlock(final Block block) {
        return this.testBlock(block, true);
    }

    Result testBlock(Block block, boolean loadChunk);

    enum Result {
        TRUE,
        FALSE,
        UNLOADED_CHUNK;

        public boolean asBoolean() {
            return this == TRUE;
        }
    }

    default @NotNull dev.gurwi.prism.bukkit.argument.type.BlockInWorldPredicate toBukkit() {
        return (block, loadChunk) -> dev.gurwi.prism.bukkit.argument.type.BlockInWorldPredicate.Result.valueOf(this.testBlock(block, loadChunk).name());
    }

}
