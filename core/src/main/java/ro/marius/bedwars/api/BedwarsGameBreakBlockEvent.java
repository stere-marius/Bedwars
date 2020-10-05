package ro.marius.bedwars.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import ro.marius.bedwars.team.Team;

public class BedwarsGameBreakBlockEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Team team;

    public BedwarsGameBreakBlockEvent(Block block, Player who, Team team) {
        super(block);
        this.player = who;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Team getTeam() {
        return this.team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
