package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class WrappedFinePosition implements WrappedPosition {

    private final double x;
    private final double y;
    private final double z;

    public WrappedFinePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int blockX() {
        return (int) Math.floor(x);
    }

    @Override
    public int blockY() {
        return (int) Math.floor(y);
    }

    @Override
    public int blockZ() {
        return (int) Math.floor(z);
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
        return false;
    }

    @Override
    public boolean isFine() {
        return true;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @Override
    public @NotNull WrappedPosition offset(int x, int y, int z) {
        return new WrappedFinePosition(this.x + x, this.y + y, this.z + z);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @Override
    public @NotNull WrappedFinePosition offset(double x, double y, double z) {
        return new WrappedFinePosition(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public @NotNull WrappedBlockPosition toBlock() {
        return new WrappedBlockPosition(blockX(), blockY(), blockZ());
    }

}