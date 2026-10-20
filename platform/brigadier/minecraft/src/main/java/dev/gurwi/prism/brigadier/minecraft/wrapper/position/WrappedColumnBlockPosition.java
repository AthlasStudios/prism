package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

public record WrappedColumnBlockPosition(int blockX, int blockZ) {

    public WrappedBlockPosition toPosition(final int y) {
        return WrappedPosition.block(blockX, y, blockZ);
    }

}
