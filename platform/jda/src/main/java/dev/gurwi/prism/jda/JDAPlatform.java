package dev.gurwi.prism.jda;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.Platform;
import net.dv8tion.jda.api.JDA;

public class JDAPlatform implements Platform<JDACommandsRegistry> {

    private final JDACommandsRegistry registry;

    private JDAPlatform(JDA jda) {
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
