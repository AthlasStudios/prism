package club.athlas.prism.manager;

import club.athlas.prism.api.PrismCommandProvider;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProvidersManager {

    private final Map<Plugin, Set<PrismCommandProvider>> providers = new HashMap<>();

    public void register(Plugin plugin, PrismCommandProvider provider) {
        providers.computeIfAbsent(plugin, p -> new HashSet<>()).add(provider);
    }

    public void unregister(Plugin plugin) {
        providers.remove(plugin);
    }

    public Set<PrismCommandProvider> getProviders() {
        return providers.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

}
