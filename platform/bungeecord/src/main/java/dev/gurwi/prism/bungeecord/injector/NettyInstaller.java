package dev.gurwi.prism.bungeecord.injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

final class NettyInstaller {

    private NettyInstaller() {
    }

    static void install(@NotNull Object channel, @NotNull String encoderName, @NotNull String handlerName, @NotNull Rewriter rewriter) {
        Channel handle = (Channel) channel;

        if (handle.pipeline().get(handlerName) != null) return;

        handle.pipeline().addBefore(encoderName, handlerName, rewriter);
    }

    static final class Rewriter extends ChannelOutboundHandlerAdapter {

        private final BungeecordCommandTreeInjector injector;
        private final ProxiedPlayer player;

        Rewriter(@NotNull BungeecordCommandTreeInjector injector, @NotNull ProxiedPlayer player) {
            this.injector = injector;
            this.player = player;
        }

        @Override
        public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
            if (injector.isCommands(message)) {
                injector.rewrite(player, message);
            }

            super.write(context, message, promise);
        }

    }

}
