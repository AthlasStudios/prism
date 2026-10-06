package dev.gurwi.prism.paper.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.bukkit.argument.IntegerArgument;
import dev.gurwi.prism.bukkit.argument.LocationArgument;
import dev.gurwi.prism.bukkit.argument.StringArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;

public class PaperDefaultMappings {

    private PaperDefaultMappings() {
    }

    public static void registerDefaults() {
        PaperMappings.register(LocationArgument.class, new PaperMapping<>() {

            @Override
            public ArgumentType<?> type(LocationArgument argument) {
                return ArgumentTypes.finePosition();
            }

            @Override
            public Object convert(LocationArgument argument, CommandSourceStack source, Object value) throws CommandSyntaxException {
                return ((FinePositionResolver) value).resolve(source).toLocation(source.getLocation().getWorld());
            }

        });

        PaperMappings.register(StringArgument.class, new PaperMapping<>() {
            @Override
            public ArgumentType<?> type(StringArgument argument) {
                return StringArgumentType.word();
            }

            @Override
            public Object convert(StringArgument argument, CommandSourceStack source, Object value) {
                return value;
            }
        });

        PaperMappings.register(IntegerArgument.class, new PaperMapping<>() {
            @Override
            public ArgumentType<?> type(IntegerArgument argument) {
                return IntegerArgumentType.integer(argument.getMin(), argument.getMax());
            }

            @Override
            public Object convert(IntegerArgument argument, CommandSourceStack source, Object value) {
                return value;
            }
        });
    }

}
