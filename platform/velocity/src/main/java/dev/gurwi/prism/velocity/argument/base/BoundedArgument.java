package dev.gurwi.prism.velocity.argument.base;

public abstract class BoundedArgument<Impl extends BoundedArgument<Impl, T>, T> extends VelocityArgument<Impl, T> {

    public BoundedArgument(String name) {
        super(name);
    }

    abstract public T getMin();

    abstract public T getMax();

}
