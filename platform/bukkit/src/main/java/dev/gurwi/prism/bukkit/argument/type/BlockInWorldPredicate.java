package dev.gurwi.prism.bukkit.argument.type;

import org.bukkit.block.Block;

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

}
