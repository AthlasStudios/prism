package dev.gurwi.prism.command.builder;

import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface CommandChildrenFactory<Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    List<? extends Definition> apply(Definition definition);

    @Contract(pure = true)
    static <Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>, Builder extends AbstractCommandBuilder<Builder, Definition, ?, ?, ?, ?>> @NotNull CommandChildrenFactory<Definition> create(List<Builder> children) {
        return def -> children.stream().map(child -> child.build(def)).collect(Collectors.toUnmodifiableList());
    }

    @Contract(pure = true)
    static <Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> @NotNull CommandChildrenFactory<Definition> empty() {
        return definition -> List.of();
    }

}
