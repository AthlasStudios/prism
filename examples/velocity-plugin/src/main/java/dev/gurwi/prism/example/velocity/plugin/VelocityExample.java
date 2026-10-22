package dev.gurwi.prism.example.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.gurwi.prism.velocity.VelocityPlatform;
import dev.gurwi.prism.velocity.argument.IntegerArgument;
import dev.gurwi.prism.velocity.command.VelocityCommand;

@Plugin(id = "prism-example", authors = "gurwi")
public class VelocityExample {

    private final VelocityPlatform commandPlatform;

    @Inject
    public VelocityExample(ProxyServer server) {
        this.commandPlatform = VelocityPlatform.configure(this, server)
                .init();
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        new VelocityCommand("prism")
                .withOptionalArguments(new IntegerArgument("n", 0, 10))
                .executes((source, args) -> {
                    source.sendPlainMessage("Hello World!");

                    args.getOptional("n").map(n -> (Integer) n).ifPresent(n -> {
                        source.sendPlainMessage("n: " + n);
                    });
                })
                .register(commandPlatform);
    }

}
