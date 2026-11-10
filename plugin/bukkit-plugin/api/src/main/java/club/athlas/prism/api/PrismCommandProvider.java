package club.athlas.prism.api;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.PrismCommand;

import java.util.Collection;

@FunctionalInterface
public interface PrismCommandProvider {

    Collection<? extends PrismCommand> getCommands();

    default BukkitCommandsRegistry<?> getRegistry() {
        return null;
    }

}
