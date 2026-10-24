package dev.gurwi.prism.bungeecord.argument.type;

public final class RangedNumber<T extends Number & Comparable<T>> {

    private final T min;
    private final T max;

    public RangedNumber(T min, T max) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Lower bound cannot exceed upper bound");
        }

        this.min = min;
        this.max = max;
    }

    public T getLowerBound() {
        return min;
    }

    public T getUpperBound() {
        return max;
    }

    public boolean isInRange(T value) {
        return (min == null || value.compareTo(min) >= 0)
                && (max == null || value.compareTo(max) <= 0);
    }

}