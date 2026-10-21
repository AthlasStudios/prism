package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class WrappedBlockPosition implements WrappedPosition {

    private final int x;
    private final int y;
    private final int z;

    public WrappedBlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int blockX() {
        return x;
    }

    @Override
    public int blockY() {
        return y;
    }

    @Override
    public int blockZ() {
        return z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public boolean isFine() {
        return false;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @Override
    public @NotNull WrappedPosition offset(int x, int y, int z) {
        return new WrappedBlockPosition(this.x + x, this.y + y, this.z + z);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @Override
    public @NotNull WrappedFinePosition offset(double x, double y, double z) {
        return new WrappedFinePosition(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public WrappedBlockPosition toBlock() {
        return this;
    }

}