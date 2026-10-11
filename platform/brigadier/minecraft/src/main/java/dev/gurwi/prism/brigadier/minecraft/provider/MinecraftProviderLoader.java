package dev.gurwi.prism.brigadier.minecraft.provider;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public final class MinecraftProviderLoader {

    private static final String PAPER_COMMANDS_CLASS = "io.papermc.paper.command.brigadier.Commands";
    private static final String PAPER_PACKAGE = "paper";
    private static final String DISPATCHER_CLASS = "CommandDispatcherProvider";
    private static final String ARGUMENT_CLASS = "ArgumentProvider";
    private static final int OLDEST_MINOR = 13;
    private static final int LAST_LEGACY_MINOR = 21;
    private static final String SUPPORT_HINT = "Please report it on our Discord, or contribute an implementation for this version.";

    private MinecraftProviderLoader() {
    }

    public static @NotNull Optional<MinecraftCommandDispatcherProvider> dispatcherProvider(@NotNull Plugin plugin) {
        return resolve(plugin, "command dispatcher", DISPATCHER_CLASS, MinecraftCommandDispatcherProvider.class, new Class<?>[]{Plugin.class}, plugin);
    }

    public static @NotNull Optional<MinecraftArgumentProvider> argumentProvider(@NotNull Plugin plugin) {
        return resolve(plugin, "argument provider", ARGUMENT_CLASS, MinecraftArgumentProvider.class, new Class<?>[0]);
    }

    public static boolean hasPaperBrigadierApi() {
        try {
            Class.forName(PAPER_COMMANDS_CLASS, false, MinecraftProviderLoader.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

    private static <T> @NotNull Optional<T> resolve(@NotNull Plugin plugin, @NotNull String label, @NotNull String className, @NotNull Class<T> type, @NotNull Class<?>[] parameterTypes, Object... arguments) {
        if (!BrigadierCompatibilityChecker.isSupported()) {
            plugin.getLogger().warning("Brigadier is not available on this server, so the " + label + " cannot be loaded");
            return Optional.empty();
        }

        ServerVersion version = ServerVersion.current();
        ClassLoader loader = MinecraftProviderLoader.class.getClassLoader();
        String packageName = MinecraftProviderLoader.class.getPackageName();
        String base = packageName.substring(0, packageName.lastIndexOf('.') + 1);
        List<String> tried = new ArrayList<>();

        for (Candidate candidate : candidates(version)) {
            String name = base + candidate.name() + "." + className;
            tried.add(candidate.name());

            try {
                Class<? extends T> implementation = Class.forName(name, true, loader).asSubclass(type);
                T instance = ReflectionUtil.instantiate(implementation, parameterTypes, arguments);

                if (candidate.kind() == Kind.OLDER) {
                    plugin.getLogger().warning("No " + label + " implementation exists for Minecraft " + describe(version) + ", using the closest older one (" + candidate.name() + "). It may not work correctly. " + SUPPORT_HINT);
                }

                return Optional.of(instance);
            } catch (ClassNotFoundException ignored) {
            } catch (InvocationTargetException exception) {
                if (!reportFailure(plugin, label, candidate, name, exception.getCause())) {
                    return Optional.empty();
                }
            } catch (ReflectiveOperationException | LinkageError | ClassCastException exception) {
                if (!reportFailure(plugin, label, candidate, name, exception)) {
                    return Optional.empty();
                }
            }
        }

        plugin.getLogger().warning("No suitable " + label + " implementation was found for Minecraft " + describe(version) + " (tried " + tried + "). " + SUPPORT_HINT);

        return Optional.empty();
    }

    private static boolean reportFailure(@NotNull Plugin plugin, @NotNull String label, @NotNull Candidate candidate, @NotNull String name, @Nullable Throwable cause) {
        boolean keepTrying = candidate.kind() == Kind.PLATFORM;
        String suffix = keepTrying ? ", trying the next implementation" : ", not trying older implementations because they would be less compatible. " + SUPPORT_HINT;

        plugin.getLogger().log(Level.WARNING, "The " + label + " implementation " + name + " failed to load" + suffix, cause);

        return keepTrying;
    }

    private static @NotNull List<Candidate> candidates(@Nullable ServerVersion version) {
        List<Candidate> result = new ArrayList<>();

        if (hasPaperBrigadierApi()) {
            result.add(new Candidate(PAPER_PACKAGE, Kind.PLATFORM));
        }

        if (version == null) {
            return result;
        }

        if (version.patch() >= 0) {
            result.add(new Candidate("V" + version.major() + "_" + version.minor() + "_" + version.patch(), Kind.CURRENT));
        }

        result.add(new Candidate("V" + version.major() + "_" + version.minor(), Kind.CURRENT));

        int major = version.major();
        int minor = version.minor();

        while (true) {
            minor--;

            if (minor < 0) {
                if (major <= 1) {
                    break;
                }

                major = 1;
                minor = LAST_LEGACY_MINOR;
            }

            if (major == 1 && minor < OLDEST_MINOR) {
                break;
            }

            result.add(new Candidate("V" + major + "_" + minor, Kind.OLDER));
        }

        return result;
    }

    private static @NotNull String describe(@Nullable ServerVersion version) {
        return version == null ? Bukkit.getBukkitVersion() : version.label();
    }

    private enum Kind {
        PLATFORM,
        CURRENT,
        OLDER
    }

    private record Candidate(String name, Kind kind) {
    }

    private record ServerVersion(int major, int minor, int patch) {

        static @Nullable ServerVersion current() {
            String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");

            try {
                int major = Integer.parseInt(parts[0]);
                int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : -1;

                return new ServerVersion(major, minor, patch);
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        @Contract(pure = true)
        @NotNull String label() {
            return patch >= 0 ? major + "." + minor + "." + patch : major + "." + minor;
        }

    }

}