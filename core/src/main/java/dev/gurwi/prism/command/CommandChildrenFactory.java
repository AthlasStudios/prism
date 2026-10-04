package dev.gurwi.prism.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface CommandChildrenFactory<Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    List<? extends Definition> apply(Definition definition);

    @Contract(pure = true)
    static <Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>, Builder extends AbstractCommandBuilder<Builder, Definition, ?, ?, ?, ?>> @NotNull CommandChildrenFactory<Definition> create(List<Builder> children) {
        return def -> children.stream().map(child -> child.build(def)).toList();
    }

    @Contract(pure = true)
    static <Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> @NotNull CommandChildrenFactory<Definition> empty() {
        return definition -> List.of();
    }

}
