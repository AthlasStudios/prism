package dev.gurwi.prism.brigadier;

public class PrismBrigadier {

    private PrismBrigadier() {
    }

    public static boolean isCompatible() {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
            Class.forName("com.mojang.brigadier.tree.CommandNode");
            Class.forName("com.mojang.brigadier.builder.LiteralArgumentBuilder");
            Class.forName("com.mojang.brigadier.builder.RequiredArgumentBuilder");
            Class.forName("com.mojang.brigadier.context.CommandContext");
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

}
