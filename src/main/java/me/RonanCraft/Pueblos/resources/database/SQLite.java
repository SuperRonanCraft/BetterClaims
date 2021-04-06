package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.files.FileOther;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class SQLite extends Database {

    String db_name = "amr_data";
    String col_nextTicketId = "NextTicketID";
    //private final boolean sqlEnabled;
    private String host, database, username, password;
    private int port;

    public String addMissingColumns = "ALTER TABLE %table% ADD COLUMN %column% %type%";

    // SQL creation stuff
    public Connection getSQLConnection() {
        if (sqlEnabled) {
            try {
                return getOnline();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Pueblos.getInstance().getLogger().info("MySQL setup is incorrect! Grabbing data from local database!");
                sqlEnabled = false;
            }
        }
        return getLocal();
    }

    private Connection getOnline() throws SQLException, ClassNotFoundException {
        synchronized (this) {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
                    "?autoReconnect=true&useSSL=false", this.username, this.password);
        }
    }

    private Connection getLocal() {
        File dataFolder = new File(Pueblos.getInstance().getDataFolder().getPath() + File.separator + "data", db_name + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.getParentFile().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e) {
                Pueblos.getInstance().getLogger().log(Level.SEVERE, "File write error: " + dataFolder.getPath());
                e.printStackTrace();
            }
        }
        try {
            if (connection!=null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        FileOther.FILETYPE sql = FileOther.FILETYPE.MYSQL;
        String pre = "MySQL.";
        sqlEnabled = sql.getBoolean(pre + "enabled");
        host = sql.getString(pre + "host");
        port = sql.getInt(pre + "port");
        database = sql.getString(pre + "database");
        username = sql.getString(pre + "username");
        password = sql.getString(pre + "password");
        table_ticket = sql.getString(pre + "tablePrefix") + "data";
        table_nextid = sql.getString(pre + "tablePrefix") + "nextid";
        connection = getSQLConnection();
        if (!sqlEnabled) { //Update table names back to default if online database fails
            table_ticket = "AMR_Data";
            table_nextid = "AMR_NextID";
        }
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(getCreateTable_ticket());
            s.executeUpdate(getCreateTable_nextid());
            //s.executeUpdate(createTable_bank);
            for (COLUMNS c : COLUMNS.values()) { //Add missing columns dynamically
                try {
                    s.executeUpdate(addMissingColumns.replace("%table%", table_ticket).replace("%column%", c.name).replace("%type%", c.type));
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            }
            //Setup Counter
            ResultSet result = s.executeQuery("SELECT " + col_nextTicketId + " FROM " + table_nextid);
            if (!result.next())
                s.executeUpdate("INSERT INTO " + table_nextid + " (" + col_nextTicketId + ") VALUES (0)");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        initialize();
    }

    public String getCreateTable_nextid() {
        return "CREATE TABLE IF NOT EXISTS " + table_nextid + "(" + col_nextTicketId + " INT " +
                "DEFAULT 0, PRIMARY KEY (" + col_nextTicketId + "))";
    }

    private String getCreateTable_ticket() {
        String str = "CREATE TABLE IF NOT EXISTS " + table_ticket + " (";
        for (COLUMNS col : COLUMNS.values()) {
            str = str.concat("`" + col.name + "` " + col.type);
            if (col.equals(COLUMNS.values()[COLUMNS.values().length - 1]))
                str = str.concat(")");
            else
                str = str.concat(", ");
        }
        //System.out.println("MySQL column string: `" + str + "`");
        return str;
    }
}
