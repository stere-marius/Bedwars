package ro.marius.bedwars.mysql;

import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLManager {

    public SQLManager() {

    }

    public void insertInto(String tableName, String whereColumn, String whereEquals, Object... values) {
        new BukkitRunnable() {

            @Override
            public void run() {

                StringBuilder builder = new StringBuilder();
                builder.append("INSERT INTO " + tableName + " VALUES (");
                for (int i = 0; i < values.length; i++) {
                    Object obj = values[i];
                    builder.append((i == (values.length - 1)) ? (obj + "") : (obj + ","));
                }
                builder.append(")");
                if (!"".equals(whereEquals) || !"".equals(whereColumn)) {
                    builder.append(" WHERE " + whereColumn + " = '" + whereEquals + "'");
                }

                BedWarsPlugin.getInstance().sql.preparedStatement(builder.toString());

            }
        }.runTaskAsynchronously(BedWarsPlugin.getInstance());
    }

    public void insertWithoutTask(String tableName, String whereColumn, String whereEquals, Object... values) {

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(tableName).append(" VALUES (");
        for (int i = 0; i < values.length; i++) {
            Object obj = values[i];
            builder.append((i == (values.length - 1)) ? (obj + "") : (obj + ","));
        }
        builder.append(")");
        if (!"".equals(whereEquals) || !"".equals(whereColumn)) {
            builder.append(" WHERE " + whereColumn + " = '" + whereEquals + "'");
        }

        BedWarsPlugin.getInstance().sql.preparedStatement(builder.toString());

    }

    public void insertPlayerTable(String tableName, Object... values) {

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(tableName);
        builder.append(" VALUES (");

        for (int i = 0; i < values.length; i++) {
            Object obj = values[i];
            builder.append((i == (values.length - 1)) ? (obj + "") : (obj + ","));
        }

        builder.append(")");

        BedWarsPlugin.getInstance().sql.preparedStatement(builder.toString());
    }

    public synchronized Object getObject(String tableName, String column, String whereColumn, String whereEquals) {
        Object obj = null;
        ResultSet set = null;

        try {
            set = BedWarsPlugin.getInstance().sql.executeQuery(
                    "SELECT " + column + " FROM " + tableName + " WHERE " + whereColumn + " = '" + whereEquals + "'");
            while (set.next()) {

                obj = set.getObject(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            BedWarsPlugin.getInstance().sql.disconnect();
        }

        return obj;
    }

    public synchronized Object getObject(String instruction) {
        Object obj = null;
        ResultSet set = null;

        try {
            set = BedWarsPlugin.getInstance().sql.executeQuery(instruction);
            while (set.next()) {

                obj = set.getObject(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            BedWarsPlugin.getInstance().sql.disconnect();
        }

        return obj;
    }

    public List<String> getTables() throws SQLException {

        Connection connection = BedWarsPlugin.getInstance().sql.getNewConnection();
        List<String> list = new ArrayList<>();

        DatabaseMetaData dbm = connection.getMetaData();

        for (String type : ManagerHandler.getGameManager().getArenaType()) {

            ResultSet tables = null;

            try {
                tables = dbm.getTables(null, null, type.toUpperCase(), null);
                if (tables.next()) {
                    list.add(type);
                }

            } finally {

                if (tables != null) {
                    tables.close();
                }

            }
        }

        return list;
    }

    public List<Object> list(String instruction) throws SQLException {

        Connection connection = BedWarsPlugin.getInstance().sql.getNewConnection();
        List<Object> objects = new ArrayList<Object>();
        try {

            try (Statement statement = connection.createStatement()) {

                try (ResultSet resultSet = statement.executeQuery(instruction)) {
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                    int columnCount = resultSetMetaData.getColumnCount();

                    while (resultSet.next()) {

                        for (int i = 1; i <= columnCount; i++) {
                            objects.add(resultSet.getObject(i));
                        }
                    }

                }
            }
        } finally {
            connection.close();
        }

        return objects;
    }

    public List<Object> getAllObjectsTwo(String instruction) throws SQLException {

        BedWarsPlugin.getInstance().sql.openConnection();

        try (Connection conn = BedWarsPlugin.getInstance().sql.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet set = stmt.executeQuery(instruction)) {
                    ResultSetMetaData resultSetMetaData = set.getMetaData();
                    int columnCount = resultSetMetaData.getColumnCount();

                    List<Object> objects = new ArrayList<>();

                    while (set.next()) {

                        for (int i = 1; i <= columnCount; i++) {
                            objects.add(set.getObject(i));
                        }
                    }
                    return objects;
                }
            }
        }
    }

    public List<Object> getAllObjects(String instruction) {

        BedWarsPlugin.getInstance().sql.openConnection();
        List<Object> objects = new ArrayList<>();
        Statement statement = null;
        ResultSet set = null;

        try {
            statement = BedWarsPlugin.getInstance().sql.getConnection().createStatement();
            set = statement.executeQuery(instruction);
//			set = BedWarsPlugin.getInstance().sql.getConnection().createStatement().executeQuery(instruction);

            ResultSetMetaData resultSetMetaData = set.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            while (set.next()) {

                for (int i = 1; i <= columnCount; i++) {
                    objects.add(set.getObject(i));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            BedWarsPlugin.getInstance().sql.disconnect();
        }

        return objects;
    }

    public Object getInteger(String tableName, String column, String whereColumn, String whereEquals) {
        int obj = 0;
        ResultSet set = null;

        try {
            set = BedWarsPlugin.getInstance().sql.executeQuery(
                    "SELECT " + column + " FROM " + tableName + " WHERE " + whereColumn + " = '" + whereEquals + "'");
            while (set.next()) {

                obj = set.getInt(column);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            BedWarsPlugin.getInstance().sql.disconnect();
        }

        return obj;
    }

    public void selectAll(String instruction, SQLCallback<List<Object>> callback) {

        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {
//				List<Object> list;
                try {
                    List<Object> list = SQLManager.this.list(instruction);
                    callback.onQueryDone(list);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//				callback.onQueryDone(list);

            }
        };

        runnable.runTaskAsynchronously(BedWarsPlugin.getInstance());

    }

    public void updateInt(String tableName, String column, int increaseWith, String whereColumn, String whereEquals) {
        new BukkitRunnable() {

            @Override
            public void run() {
                BedWarsPlugin.getInstance().sql.preparedStatement("UPDATE " + tableName + " SET " + column + "=" + column + "+"
                        + increaseWith + " WHERE " + whereColumn + " = '" + whereEquals + "'");

            }
        }.runTaskAsynchronously(BedWarsPlugin.getInstance());

    }

    public void updateString(String tableName, String column, String whereColumn, String whereEquals) {
        new BukkitRunnable() {

            @Override
            public void run() {
                BedWarsPlugin.getInstance().sql.preparedStatement("UPDATE " + tableName + " SET " + column + "=" + column
                        + " WHERE " + whereColumn + " = '" + whereEquals + "'");

            }
        }.runTaskAsynchronously(BedWarsPlugin.getInstance());

    }

    public void updateString(String tableName, String column, String replaceWith, String whereColumn,
                             String whereEquals) {
        new BukkitRunnable() {

            @Override
            public void run() {
                BedWarsPlugin.getInstance().sql.preparedStatement("UPDATE " + tableName + " SET " + column + " = '"
                        + replaceWith + "' WHERE " + whereColumn + " = '" + whereEquals + "'");

            }
        }.runTaskAsynchronously(BedWarsPlugin.getInstance());

    }

    public boolean containsPlayer(String tableName, String whereColumn, UUID uuid) {
        return BedWarsPlugin.getInstance().sql.contains(tableName, whereColumn, uuid);
    }

}
