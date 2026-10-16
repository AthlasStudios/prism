package dev.gurwi.prism.jda.command;

import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.jda.argument.JDAArgument;
import dev.gurwi.prism.jda.executor.JDACommandExecutor;
import dev.gurwi.prism.jda.permission.CommandPermission;
import dev.gurwi.prism.requirement.UsageRequirement;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JDACommandDefinition extends AbstractCommandDefinition<JDACommandDefinition, User, JDACommandExecutor, JDAArgument<?, ?>, CommandPermission> {

    protected JDACommandDefinition(@Nullable JDACommandDefinition parent, String name, List<String> aliases, String description, UsageRequirement<User> requirement, Set<CommandPermission> commandPermissions, List<JDAArgument<?, ?>> requiredArguments, List<JDAArgument<?, ?>> optionalArguments, Map<Class<? extends JDACommandExecutor>, JDACommandExecutor> executors, @NotNull CommandChildrenFactory<JDACommandDefinition> childrenFactory) {
        super(parent, name, aliases, description, requirement, commandPermissions, requiredArguments, optionalArguments, executors, childrenFactory);
    }

    protected JDACommandDefinition(String name, List<String> aliases, String description, UsageRequirement<User> requirement, Set<CommandPermission> commandPermissions, List<JDAArgument<?, ?>> requiredArguments, List<JDAArgument<?, ?>> optionalArguments, Map<Class<? extends JDACommandExecutor>, JDACommandExecutor> executors, CommandChildrenFactory<JDACommandDefinition> childrenFactory) {
        super(name, aliases, description, requirement, commandPermissions, requiredArguments, optionalArguments, executors, childrenFactory);
    }

}
