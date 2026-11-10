package dev.gurwi.prism.jda;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.CommandPlatform;
import net.dv8tion.jda.api.JDA;

public class JDACommandPlatform implements CommandPlatform<JDACommandsRegistry> {

    private final JDACommandsRegistry registry;

    private JDACommandPlatform(JDA jda) {
        this.registry = new JDACommandsRegistry();
    }

    @Override
    public String getIdentifier() {
        return Prism.JDA_PLATFORM_IDENTIFIER;
    }

    @Override
    public JDACommandsRegistry getCommandsRegistry() {
        return registry;
    }

}
