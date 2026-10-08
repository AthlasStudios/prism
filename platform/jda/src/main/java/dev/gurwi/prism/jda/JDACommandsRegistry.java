package dev.gurwi.prism.jda;

import dev.gurwi.prism.jda.command.JDACommandDefinition;
import dev.gurwi.prism.platform.CommandsRegistry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class JDACommandsRegistry implements CommandsRegistry<JDAPlatform, JDACommandDefinition> {

    @Override
    public void register(JDACommandDefinition definition) {

    }

    @Override
    public void unregister(JDACommandDefinition definition) {

    }

    @Override
    public @Unmodifiable Set<JDACommandDefinition> getRegisteredCommands() {
        return Set.of();
    }

    @Override
    public JDAPlatform getPlatform() {
        return null;
    }

}
