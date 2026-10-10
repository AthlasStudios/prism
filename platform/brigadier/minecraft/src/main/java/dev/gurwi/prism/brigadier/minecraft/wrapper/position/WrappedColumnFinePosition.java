package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

public class WrappedColumnFinePosition {

    private final double x;
    private final double z;

    public WrappedColumnFinePosition(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double x() {
        return x;
    }

    public double z() {
        return z;
    }

    public WrappedFinePosition toPosition(final double y) {
        return WrappedPosition.fine(this.x(), y, this.z());
    }

}
