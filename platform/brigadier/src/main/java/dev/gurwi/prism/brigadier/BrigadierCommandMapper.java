package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.executable.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BrigadierCommandMapper<S> {

    public @NotNull List<LiteralCommandNode<S>> map(@NotNull CommandDefinition<?, ?> definition) {
        List<LiteralCommandNode<S>> nodes = new ArrayList<>();

        nodes.add(mapLiteral(definition, definition.getName()));

        for (String alias : definition.getAliases()) {
            nodes.add(mapLiteral(definition, alias));
        }

        return nodes;
    }

    private LiteralCommandNode<S> mapLiteral(@NotNull CommandDefinition<?, ?> definition, @NotNull String namesd) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(name);

        for (CommandDefinition<?, ?> child : definition.getChildren()) {
            for (LiteralCommandNode<S> childNode : map(child)) {
                builder.then(childNode);
            }
        }

        return builder.build();
    }

}