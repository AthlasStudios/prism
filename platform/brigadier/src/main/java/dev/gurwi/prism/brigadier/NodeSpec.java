package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.gurwi.prism.argument.AbstractArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NodeSpec {

    private final AbstractArgument<?, ?, ?, ?> argument;
    private final String name;
    private final ArgumentType<?> type;
    private final boolean terminal;
    private final boolean greedy;
    private final boolean optional;
    private final int part;
    private final int parts;
    private final boolean literal;

    public NodeSpec(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String name, @NotNull ArgumentType<?> type,
                    boolean terminal, boolean greedy, boolean optional, int part, int parts, boolean literal) {
        this.argument = argument;
        this.name = name;
        this.type = type;
        this.terminal = terminal;
        this.greedy = greedy;
        this.optional = optional;
        this.part = part;
        this.parts = parts;
        this.literal = literal;
    }

    public @NotNull AbstractArgument<?, ?, ?, ?> argument() {
        return argument;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull ArgumentType<?> type() {
        return type;
    }

    public boolean terminal() {
        return terminal;
    }

    public boolean greedy() {
        return greedy;
    }

    public boolean optional() {
        return optional;
    }

    public int part() {
        return part;
    }

    public int parts() {
        return parts;
    }

    public boolean literal() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeSpec)) return false;
        NodeSpec other = (NodeSpec) o;
        return terminal == other.terminal
                && greedy == other.greedy
                && optional == other.optional
                && part == other.part
                && parts == other.parts
                && literal == other.literal
                && Objects.equals(argument, other.argument)
                && Objects.equals(name, other.name)
                && Objects.equals(type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, name, type, terminal, greedy, optional, part, parts, literal);
    }

    @Override
    public String toString() {
        return "Slot[argument=" + argument + ", name=" + name + ", type=" + type
                + ", terminal=" + terminal + ", greedy=" + greedy + ", optional=" + optional
                + ", part=" + part + ", parts=" + parts + ", literal=" + literal + "]";
    }

}