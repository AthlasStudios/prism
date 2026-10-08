package dev.gurwi.prism.paper.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface PaperArgumentMapping<A extends BukkitArgument<?, ?>> {

    ArgumentType<?> type(A argument);

    Object convert(A argument, CommandSourceStack source, Object value) throws CommandSyntaxException;

}