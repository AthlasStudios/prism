package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.requirement.ExecutionRequirement;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class BukkitCommandDefinition extends AbstractCommandDefinition<CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>> {

    protected BukkitCommandDefinition(String name, List<String> aliases, String description, ExecutionRequirement<CommandSender> requirement, List<BukkitArgument<?, ?>> requiredArguments, List<BukkitArgument<?, ?>> optionalArguments, Map<Class<? extends BukkitCommandExecutor<? extends CommandSender>>, BukkitCommandExecutor<? extends CommandSender>> executors) {
        super(name, aliases, description, requirement, requiredArguments, optionalArguments, executors);
    }

}
