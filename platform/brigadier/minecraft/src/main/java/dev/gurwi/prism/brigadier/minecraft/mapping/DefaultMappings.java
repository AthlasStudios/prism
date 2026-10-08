package dev.gurwi.prism.brigadier.minecraft.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.gurwi.prism.brigadier.minecraft.MinecraftBrigadierSource;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;

public class DefaultMappings {

    private DefaultMappings() {
    }

    public static void registerDefaults() {
//        PaperMappings.register(LocationArgument.class, new PaperMapping<>() {
//
//            @Override
//            public ArgumentType<?> type(LocationArgument argument) {
//                return ArgumentTypes.finePosition();
//            }
//
//            @Override
//            public Object convert(LocationArgument argument, MinecraftBrigadierSource source, Object value) throws CommandSyntaxException {
//                return ((FinePositionResolver) value).resolve(source).toLocation(source.getLocation().getWorld());
//            }
//
//        });

        MinecraftMappings.register(StringArgument.class, new MinecraftArgumentMapping<>() {
            @Override
            public ArgumentType<?> type(StringArgument argument) {
                return StringArgumentType.word();
            }

            @Override
            public Object convert(StringArgument argument, MinecraftBrigadierSource source, Object value) {
                return value;
            }
        });

        MinecraftMappings.register(IntegerArgument.class, new MinecraftArgumentMapping<>() {
            @Override
            public ArgumentType<?> type(IntegerArgument argument) {
                return IntegerArgumentType.integer(argument.getMin(), argument.getMax());
            }

            @Override
            public Object convert(IntegerArgument argument, MinecraftBrigadierSource source, Object value) {
                return value;
            }
        });
    }

}
