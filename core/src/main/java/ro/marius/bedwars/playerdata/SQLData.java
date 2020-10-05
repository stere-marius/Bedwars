package ro.marius.bedwars.playerdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.mysql.SQLCallback;
import ro.marius.bedwars.mysql.SQLManager;
import ro.marius.bedwars.utils.Utils;

import java.util.*;
import java.util.Map.Entry;

interface CallBack {

    void onSuccess();

    void onError();

}

public class SQLData extends APlayerData {

    public SQLData(Player player) {
        super(player);
    }

    @Override
    public void loadData() {

        Set<String> arenaType = ManagerHandler.getGameManager().getArenaType();

        arenaType.forEach(t -> this.getArenaData().put(t, new ArenaData()));

        this.checkPlayer(new CallBack() {

            @Override
            public void onSuccess() {
                SQLData.this.assignValues();

            }

            @Override
            public void onError() {
                Bukkit.getConsoleSender()
                        .sendMessage(Utils.translate("&c[BEDWARS] An MySQL error has occured when player "
                                + SQLData.this.getPlayer().getUniqueId() + " data was loading."));

            }
        });

    }

    public void assignValues() {

        UUID uuid = this.getPlayer().getUniqueId();
        Set<String> tableList = ManagerHandler.getGameManager().getArenaType();

        for (String type : tableList) {

            ManagerHandler.getSQLManager().selectAll(
                    "SELECT * FROM `" + type.toUpperCase() + "` WHERE UUID = '" + uuid + "'",
                    new SQLCallback<List<Object>>() {

                        @Override
                        public void onQueryDone(List<Object> result) {

                            int gamesPlayed = (int) result.get(1);
                            int bedsBroken = (int) result.get(2);
                            int bedsLost = (int) result.get(3);
                            int kills = (int) result.get(4);
                            int deaths = (int) result.get(5);
                            int finalKills = (int) result.get(6);
                            int finalDeaths = (int) result.get(7);
                            int wins = (int) result.get(8);
                            int losses = (int) result.get(9);
                            String quickBuy = (String) result.get(10);

                            SQLData.this.getArenaData().put(type, new ArenaData(gamesPlayed, bedsBroken, bedsLost, kills, deaths,
                                    finalKills, finalDeaths, wins, losses, SQLData.this.getDeserializedMap(quickBuy)));

                        }
                    });

        }

//		ManagerHandler.getSQLManager().

    }

    public void checkPlayer(CallBack callback) {

        UUID uuid = this.getPlayer().getUniqueId();

        SQLManager sqlManager = ManagerHandler.getSQLManager();

        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    for (String type : ManagerHandler.getGameManager().getArenaType()) {

                        String typeUpper = type.toUpperCase();

                        if (!sqlManager.containsPlayer(typeUpper, "UUID", uuid)) {
//							INSERT INTO `DEFAULT` VALUES ('bc220f8d-8ba4-3119-911b-1bbb9722692d',0,0,0,0,0,0,0,0,0,'')
                            sqlManager.insertPlayerTable("`" + typeUpper + "`", "\'" + uuid + "\'", 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, "\'\'");
                        }

                    }

                    if (!sqlManager.containsPlayer("SKINS", "UUID", uuid)) {
                        sqlManager.insertPlayerTable("`SKINS`", "\'" + uuid + "\'", "'DEFAULT'");
                    }

//					if (!sqlManager.containsPlayer("BEDWARS-LEVEL", "UUID", uuid)) {
//						sqlManager.insertPlayerTable("`BEDWARS-LEVEL`", "\'" + uuid.toString() + "\'", 0, 0);
//					}

                    callback.onSuccess();

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError();
                }

            }
        };

        runnable.runTaskAsynchronously(BedWarsPlugin.getInstance());

    }

    @Override
    public void saveData() {

    }

    public void saveQuickBuy() {
        SQLManager sqlManager = ManagerHandler.getSQLManager();
        String uuid = this.getPlayer().getUniqueId().toString();

        for (Entry<String, ArenaData> entry : this.getArenaData().entrySet()) {

            StringBuilder builder = new StringBuilder();

            entry.getValue().getQuickBuy().forEach((k, v) -> builder.append(k).append(";").append(v).append(","));

            String sql = builder.toString();

            if (sql.isEmpty()) {
                continue;
            }

            sql = sql.substring(0, sql.length() - 1);
//
//			"UPDATE " + tableName + " SET " + column + "=" + column
//			+ " WHERE " + whereColumn + " = '" + whereEquals + "'"

            sqlManager.updateString("`" + entry.getKey().toUpperCase() + "`", "QuickBuy", sql, "UUID", uuid);

        }
    }

    @Override
    public void addGamePlayed(String arenaType) {

        super.addGamePlayed(arenaType);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "GamesPlayed", 1, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addWin(String arenaType) {

        super.addWin(arenaType);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "Wins", 1, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addDefeat(String arenaType) {

        super.addDefeat(arenaType);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "Defeats", 1, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addKills(String arenaType, int amount) {

        if (amount == 0) {
            return;
        }

        super.addKills(arenaType, amount);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "Kills", amount, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addDeaths(String arenaType, int amount) {

        if (amount == 0) {
            return;
        }

        super.addDeaths(arenaType, amount);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "Deaths", amount, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void setSkin(String skin) {

        if ((skin == null) || "".equals(skin)) {
            return;
        }

        super.setSkin(skin);

        ManagerHandler.getSQLManager().updateString("`Skins`", "Skin", "UUID", this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addBedLost(String arenaType) {

        super.addBedLost(arenaType);

        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "BedsLost", 1, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addFinalKills(String arenaType, int amount) {

        if (amount == 0) {
            return;
        }

        super.addFinalKills(arenaType, amount);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "FinalKills", amount, "UUID",
                this.getPlayer().getUniqueId().toString());

    }

    @Override
    public void addBedsBroken(String arenaType, int amount) {

        if (amount == 0) {
            return;
        }

        super.addBedsBroken(arenaType, amount);
        ManagerHandler.getSQLManager().updateInt("`" + arenaType.toUpperCase() + "`", "BedsBroken", amount, "UUID",
                this.getPlayer().getUniqueId().toString());
    }

    public Map<Integer, String> getDeserializedMap(String s) {

        if (!s.contains(",") || !s.contains(";")) {
            return new HashMap<Integer, String>();
        }

        Map<Integer, String> map = new HashMap<>();

        String[] split = s.split(",");

        int index = 0;

        for (String c : split) {

            if (!c.contains(";")) {
                continue;
            }

            String comma[] = c.split(";");
            Integer slot = Utils.getInteger(comma[0]);

            map.put((slot == -1) ? index : slot, comma[1]);

            index++;
        }

        return map;
    }

    @Override
    public void loadData(String arenaType) {

        UUID uuid = this.getPlayer().getUniqueId();
        new BukkitRunnable() {

            @Override
            public void run() {

                if (!ManagerHandler.getSQLManager().containsPlayer(arenaType.toUpperCase(), "UUID", uuid)) {

                    BedWarsPlugin.getInstance().sql.preparedStatement("INSERT INTO `" + arenaType.toUpperCase() + "` VALUES('"
                            + uuid + "' , 0, 0, 0, 0, 0, 0, 0, 0, 0, '')");
                    return;
                }

                String sql = "SELECT * FROM `" + arenaType.toUpperCase() + "` WHERE UUID = '" + uuid + "'";

                ManagerHandler.getSQLManager().selectAll(sql, new SQLCallback<List<Object>>() {

                    @Override
                    public void onQueryDone(List<Object> result) {

                        if (result.size() < 11) {
                            return;
                        }

                        int gamesPlayed = (int) result.get(1);
                        int bedsBroken = (int) result.get(2);
                        int bedsLost = (int) result.get(3);
                        int kills = (int) result.get(4);
                        int deaths = (int) result.get(5);
                        int finalKills = (int) result.get(6);
                        int finalDeaths = (int) result.get(7);
                        int wins = (int) result.get(8);
                        int losses = (int) result.get(9);
                        String quickBuy = (String) result.get(10);

                        SQLData.this.getArenaData().put(arenaType, new ArenaData(gamesPlayed, bedsBroken, bedsLost, kills, deaths,
                                finalKills, finalDeaths, wins, losses, SQLData.this.getDeserializedMap(quickBuy)));
                    }
                });

            }
        }.runTaskAsynchronously(BedWarsPlugin.getInstance());

        if (!this.getArenaData().containsKey(arenaType)) {
            this.getArenaData().put(arenaType, new ArenaData());
        }

    }
}
