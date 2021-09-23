package ro.marius.bedwars.npc.bedwars;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import org.bukkit.entity.Player;

public class PacketInjector {
    private Channel channel;

    public void removePlayer(Player player) {
        this.channel.eventLoop().submit(() -> {
            this.channel.pipeline().remove("BedwarsPlugin");
            return null;
        });
    }

    public void injectPlayer(Player player, ChannelDuplexHandler channelDuplexHandler) {

        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object con = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Object network = con.getClass().getField("networkManager").get(con);
            this.channel = (Channel) network.getClass().getField("channel").get(network);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.channel.pipeline().get("BedwarsPlugin") == null) {
            this.channel.pipeline().addBefore("packet_handler", "BedwarsPlugin", channelDuplexHandler);
        }

    }
}
