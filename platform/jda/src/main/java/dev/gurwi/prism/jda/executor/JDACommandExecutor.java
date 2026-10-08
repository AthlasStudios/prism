package dev.gurwi.prism.jda.executor;

import dev.gurwi.prism.command.CommandExecutor;
import dev.gurwi.prism.jda.command.JDACommandDefinition;
import net.dv8tion.jda.api.entities.User;

public interface JDACommandExecutor extends CommandExecutor<User, JDACommandDefinition> {
}
