package dev.gurwi.prism.test.plugin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.gurwi.prism.brigadier.minecraft.argument.BrigadierArgument;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BrigadierStringArgument extends BrigadierArgument<BrigadierStringArgument, String> {

    public BrigadierStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(@NonNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        return value;
    }

    @Override
    public String map(BrigadierStringArgument brigadierStringArgument, MinecraftCommandSource source, String value) {
        return value;
    }

    @Override
    public ArgumentType<String> toBrigadierType(BrigadierStringArgument argument) {
        return StringArgumentType.string();
    }

}
