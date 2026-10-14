package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class WrappedColumnFinePosition {

    private final double x;
    private final double z;

    public WrappedColumnFinePosition(double x, double z) {
        this.x = x;
        this.z = z;
    }

    @Contract("_ -> new")
    public @NotNull WrappedFinePosition toPosition(final double y) {
        return WrappedPosition.fine(this.getX(), y, this.getZ());
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }


}
