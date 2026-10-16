package dev.gurwi.prism.brigadier.minecraft;

import org.bukkit.Bukkit;

public class BrigadierCompatibilityChecker {

    private static final String[] REQUIRED = {
            "com.mojang.brigadier.CommandDispatcher",
            "com.mojang.brigadier.tree.LiteralCommandNode",
            "com.mojang.brigadier.suggestion.Suggestions"
    };

    private static Boolean supported;

    private BrigadierCompatibilityChecker() {
    }

    public static synchronized boolean isSupported() {
        if (supported == null) supported = detect();
        return supported;
    }

    public static boolean has(String className) {
        try {
            Class.forName(className, false, loader());
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

    private static boolean detect() {
        for (String name : REQUIRED) {
            if (!has(name)) return false;
        }

        return true;
    }

    private static ClassLoader loader() {
        return Bukkit.getServer().getClass().getClassLoader();
    }

}