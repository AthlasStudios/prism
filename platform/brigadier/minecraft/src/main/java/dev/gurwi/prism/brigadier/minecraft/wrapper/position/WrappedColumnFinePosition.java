package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

public record WrappedColumnFinePosition(double x, double z) {

    public WrappedFinePosition toPosition(final double y) {
        return WrappedPosition.fine(this.x(), y, this.z());
    }

}
