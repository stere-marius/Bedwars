package ro.marius.bedwars.mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;

import java.sql.*;
import java.util.UUID;

public class MySQL {

    private Connection connection;
    private String user;
    private String database;
    private String password;
    private int port;
    private String hostName;

    public MySQL(String hostName, int port, String database, String username, String password) {
        this.hostName = hostName;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    public void createDatabase() {

        String databaseName = BedWarsPlugin.getInstance().getConfig().getString("MySQL.Database");

        if ((databaseName == null) || databaseName.isEmpty()) {
            System.out.println("[Bedwars] ERROR: Check your MySQL.Database from config.yml, it must have a name.");
            return;
        }

        Connection connection = null;
        String url = "jdbc:mysql://" + this.hostName + ":" + this.port + "?useSSQL=false";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, this.user, this.password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE " + databaseName);
            statement.close();
            connection.close();
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.GREEN + "[Bedwars] The database " + databaseName + " has been created.");
        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN
                    + "[Bedwars] Unable to make the connection with MySQL database.Check if the entered data in config.yml is correct.");
            e.printStackTrace();
        } catch (SQLException e) {

            if (e.getErrorCode() != 1007) {
                e.printStackTrace();
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public Connection getNewConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + this.hostName + ":" + this.port + "/"
                    + this.database + "?autoReconnect=true&useSSL=false", this.user, this.password);
            this.connection = connection;

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void createNewConnection() {

    }

    public boolean checkConnection() {

        try {
            return (this.connection != null) && this.connection.isClosed();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }

    public void openConnection() {
        try {
            if ((this.connection != null) && !this.connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if ((this.connection != null) && !this.connection.isClosed()) {
                    return;
                }

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {

                    e.printStackTrace();
                }

                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostName + ":" + this.port + "/"
                        + this.database + "?autoReconnect=true&useSSL=false", this.user, this.password);
            }
        } catch (SQLException e) {

            System.out.println(
                    "Unable to make connection with MySQL database.Check if the data in config.yml is correct.");
            e.printStackTrace();
        }
    }

    public synchronized void createTables() {

        this.openConnection();

        System.out.println("[Bedwars] Loaded MySQL stats.");

        for (String type : ManagerHandler.getGameManager().getArenaType()) {
            this.execute("CREATE TABLE IF NOT EXISTS `" + type.toUpperCase() + "` " + "(UUID VARCHAR(37) PRIMARY KEY, "
                    + "GamesPlayed INT(8) DEFAULT 0, " + "BedsBroken INT(8) DEFAULT 0, " + "BedsLost INT(8) DEFAULT 0,"
                    + "Kills INT(8) DEFAULT 0, " + "Deaths INT(8) DEFAULT 0, " + "FinalKills INT(8) DEFAULT 0, "
                    + "FinalDeaths INT(8) DEFAULT 0, " + "Wins INT(8) DEFAULT 0, " + "Defeats INT(8) DEFAULT 0, "
                    + "QuickBuy VARCHAR(10000) DEFAULT '')");
        }

        this.execute("CREATE TABLE IF NOT EXISTS SKINS(UUID VARCHAR(37) PRIMARY KEY, Skin VARCHAR(20))");
//		execute("CREATE TABLE IF NOT EXISTS BEDWARS-LEVEL(UUID VARCHAR(37) PRIMARY KEY, Level INT(8), Progress INT(10)");

        this.disconnect();

    }

    public void execute(String sql) {

        this.openConnection();

        try (Statement statement = this.connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void preparedStatement(String sql) {

        this.openConnection();


        try {
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            this.disconnect();
        }

    }

    public ResultSet executeQuery(String sql) {

        this.openConnection();

        ResultSet resultSet = null;

        try {
            resultSet = this.connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if ((resultSet != null) && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return resultSet;
    }

    public boolean isClosed() {

        try {
            return (this.connection == null) && this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }

    }

    public boolean contains(String tableName, String whereColumn, UUID uuid) {

        this.openConnection();
        String sql = "SELECT * FROM `" + tableName + "` WHERE " + whereColumn + " = '" + uuid + "'";

        try (Statement statement = this.connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.disconnect();
        }
    }

    public void disconnect() {

        if (!this.isClosed()) {
            return;
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void closeConnection() {

        try {
            if ((this.connection == null) || this.connection.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Utils.translate("[BEDWARS] Couldn't close the connection."));
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
