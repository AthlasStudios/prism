package club.athlas.prism.config;

import dev.gurwi.confetti.annotation.Config;
import dev.gurwi.confetti.annotation.Path;

@Config(value = "config.yml", defaultResource = "config.yml", autoSave = true)
public class PrismConfiguration {

    @Path("disableInfoCommand")
    public static boolean disableInfoCommand = false;

    @Path("disableBrigadierRegistrar")
    public static boolean disableBrigadierRegistrar = false;

    @Path("disableLegacyRegistrar")
    public static boolean disableLegacyRegistrar = false;

    @Path("disableAsyncSuggestions")
    public static boolean disableAsyncSuggestions = false;

    @Path("registerPermissions")
    public static boolean registerPermissions = false;

}
