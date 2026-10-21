package club.athlas.prism.api;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;

import java.util.Collection;

@FunctionalInterface
public interface PrismCommandProvider {

    Collection<? extends PrismBukkitCommand> getCommands();

    default BukkitCommandsRegistry<?> getRegistry() {
        return null;
    }

}
