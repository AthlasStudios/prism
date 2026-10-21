package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;

public interface WrappedPosition {

    WrappedFinePosition FINE_ZERO = new WrappedFinePosition(0, 0, 0);
    WrappedBlockPosition BLOCK_ZERO = new WrappedBlockPosition(0, 0, 0);

    int blockX();
    
    int blockY();
    
    int blockZ();

    double x();

    double y();

    double z();
    
    boolean isBlock();

    boolean isFine();
    
    default boolean isFinite() {
        return Double.isFinite(this.x()) && Double.isFinite(this.y()) && Double.isFinite(this.z());
    }
    
    WrappedPosition offset(int x, int y, int z);

    WrappedFinePosition offset(double x, double y, double z);

    @Contract(value = "-> new", pure = true)
    default WrappedFinePosition toCenter() {
        return new WrappedFinePosition(this.blockX() + 0.5, this.blockY() + 0.5, this.blockZ() + 0.5);
    }
    
    @Contract(pure = true)
    WrappedBlockPosition toBlock();
    
    @Contract(value = "-> new", pure = true)
    default Vector toVector() {
        return new Vector(this.x(), this.y(), this.z());
    }
    
    @Contract(value = "_ -> new", pure = true)
    default Location toLocation(final World world) {
        return new Location(world, this.x(), this.y(), this.z());
    }
    
    @Contract(value = "_, _, _ -> new", pure = true)
    static WrappedBlockPosition block(final int x, final int y, final int z) {
        return new WrappedBlockPosition(x, y, z);
    }
    
    @Contract(value = "_ -> new", pure = true)
    static WrappedBlockPosition block(final Location location) {
        return new WrappedBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    @Contract(value = "_, _, _ -> new", pure = true)
    static WrappedFinePosition fine(final double x, final double y, final double z) {
        return new WrappedFinePosition(x, y, z);
    }

    @Contract(value = "_ -> new", pure = true)
    static WrappedFinePosition fine(final Location location) {
        return new WrappedFinePosition(location.getX(), location.getY(), location.getZ());
    }
    
    
}
