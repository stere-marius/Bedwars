package ro.marius.bedwars.manager.type;

import org.bukkit.plugin.PluginManager;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.listeners.*;
import ro.marius.bedwars.listeners.bungeecord.BungeeJoin;
import ro.marius.bedwars.listeners.bungeecord.BungeeLogin;
import ro.marius.bedwars.listeners.bungeecord.BungeePing;
import ro.marius.bedwars.listeners.bungeecord.BungeeQuit;
import ro.marius.bedwars.listeners.game.MobSpawnInArena;
import ro.marius.bedwars.listeners.game.entity.*;
import ro.marius.bedwars.listeners.game.players.*;
import ro.marius.bedwars.listeners.game.spectators.*;
import ro.marius.bedwars.listeners.playerdata.PlayerDataJoin;
import ro.marius.bedwars.listeners.playerdata.PlayerDataQuit;
import ro.marius.bedwars.listeners.waiting.*;
import ro.marius.bedwars.npc.JoinNpcListener;
import ro.marius.bedwars.utils.XMaterial;

public class ListenerManager {

    public ListenerManager() {

    }

    public void registerEvents(BedWarsPlugin plugin) {

        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new InventoryClick(), plugin);
        manager.registerEvents(new GameCuboidSelect(), plugin);
        manager.registerEvents(new ArenaWorldEvent(), plugin);
        manager.registerEvents(new PlayerInteractNPC(), plugin);
        manager.registerEvents(new PlayerBlockBreak(), plugin);
        manager.registerEvents(new PlayerDeathGame(), plugin);
        manager.registerEvents(new PlayerDeathVoid(), plugin);
        manager.registerEvents(new FireballExplode(), plugin);
        manager.registerEvents(new BungeeJoin(), plugin);
        manager.registerEvents(new BungeeLogin(), plugin);
        manager.registerEvents(new BungeePing(), plugin);
        manager.registerEvents(new BungeeQuit(), plugin);
        manager.registerEvents(new IceFishEvent(), plugin);
        manager.registerEvents(new IronGolemEvent(), plugin);
        manager.registerEvents(new PlayerBlockPlace(), plugin);
        manager.registerEvents(new PlayerChatGame(), plugin);
        manager.registerEvents(new PlayerClickArmor(), plugin);
        manager.registerEvents(new PlayerCraftEvent(), plugin);
        manager.registerEvents(new PlayerDamageGame(), plugin);
        manager.registerEvents(new PlayerDrinkPotions(), plugin);
        manager.registerEvents(new PlayerDropItem(), plugin);
        manager.registerEvents(new PlayerInteractBed(), plugin);
        manager.registerEvents(new PlayerInteractFireball(), plugin);
        manager.registerEvents(new PlayerInteractGenerators(), plugin);
        manager.registerEvents(new PlayerInteractItems(), plugin);
        manager.registerEvents(new PlayerRejoin(), plugin);
        manager.registerEvents(new PlayerJoin(), plugin);
        manager.registerEvents(new PlayerKnockback(), plugin);
        manager.registerEvents(new PlayerPlaceBucket(), plugin);
        manager.registerEvents(new PlayerPlaceTNT(), plugin);
        manager.registerEvents(new PlayerProcessCommand(), plugin);
        manager.registerEvents(new PlayerQuit(), plugin);
        manager.registerEvents(new PlayerInteractEgg(), plugin);
        manager.registerEvents(new PlayerSpawnEntity(), plugin);
        manager.registerEvents(new TeammateDamage(), plugin);
        manager.registerEvents(new WaitingBlockEvent(), plugin);
        manager.registerEvents(new WaitingChat(), plugin);
        manager.registerEvents(new WaitingItemsDrop(), plugin);
        manager.registerEvents(new WaitingMoveItem(), plugin);
        manager.registerEvents(new WaitingPlayerDamage(), plugin);
        manager.registerEvents(new HologramListener(), plugin);
        manager.registerEvents(new PlayerDataJoin(), plugin);
        manager.registerEvents(new PlayerDataQuit(), plugin);
        manager.registerEvents(new PlayerArenaSign(), plugin);
        manager.registerEvents(new SpectatorChat(), plugin);
        manager.registerEvents(new SpectatorCreativeInventory(), plugin);
        manager.registerEvents(new SpectatorDamage(), plugin);
        manager.registerEvents(new SpectatorLeaveBed(), plugin);
        manager.registerEvents(new SpectatorLoseHunger(), plugin);
        manager.registerEvents(new SpectatorMoveEvent(), plugin);
        manager.registerEvents(new SpectatorOpenChest(), plugin);
        manager.registerEvents(new SpectatorInteractItems(), plugin);
        manager.registerEvents(new SpectatorDropItem(), plugin);
        manager.registerEvents(new SpectatorTargetGolem(), plugin);
        manager.registerEvents(new TNTExplode(), plugin);
        manager.registerEvents(new RestartListener(), plugin);
        manager.registerEvents(new PlayerTeleportGame(), plugin);
        manager.registerEvents(new MobSpawnInArena(), plugin);
        manager.registerEvents(XMaterial.isNewVersion() ? new NewPickupEvent() : new PlayerPickItem(),
                plugin);
        manager.registerEvents(new ItemMergeEvent(), plugin);
        manager.registerEvents(new PlayerHunger(), plugin);
        manager.registerEvents(new PlayerDrinkMilk(), plugin);
        manager.registerEvents(new SpectatorToggleFly(), plugin);
        manager.registerEvents(new GameEditListener(), plugin);
        manager.registerEvents(new JoinNpcListener(), plugin);

//        if (!XMaterial.isNewVersion()) {
//            manager.registerEvents(new PlayerDamageArmor(), plugin);
//        }


    }

}
