package dev.gurwi.prism.jda.command;

import dev.gurwi.prism.command.builder.AbstractCommandBuilder;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.jda.argument.JDAArgument;
import dev.gurwi.prism.jda.executor.JDACommandExecutor;
import dev.gurwi.prism.jda.permission.CommandPermission;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JDACommandBuilder extends AbstractCommandBuilder<JDACommandBuilder, JDACommandDefinition, User, JDACommandExecutor, JDAArgument<?, ?>, CommandPermission> {

    public JDACommandBuilder(String name, List<String> aliases) {
        super(name, aliases);
    }

    public JDACommandBuilder(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public JDACommandDefinition build(@Nullable JDACommandDefinition parent) {
        return new JDACommandDefinition(
                parent,
                name,
                aliases,
                description,
                requirement,
                permissions,
                requiredArguments,
                optionalArguments,
                executors,
                CommandChildrenFactory.create(children)
        );
    }

}
