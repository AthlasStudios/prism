package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BlockStateArgument extends BukkitArgument<BlockStateArgument, BlockData> {

    public BlockStateArgument(String name) {
        super(name);
    }

    @Override
    public BlockData parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Bukkit.createBlockData(value);
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a block state got " + value);
        }
    }

}