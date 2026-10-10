package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

public class WrappedColumnBlockPosition {

    private final int blockX;
    private final int blockZ;

    public WrappedColumnBlockPosition(int blockX, int blockZ) {
        this.blockX = blockX;
        this.blockZ = blockZ;
    }

    public WrappedBlockPosition toPosition(final int y) {
        return WrappedPosition.block(blockX, y, blockZ);
    }

}
