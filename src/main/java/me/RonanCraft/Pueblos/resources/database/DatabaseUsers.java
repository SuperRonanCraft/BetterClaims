package me.RonanCraft.Pueblos.resources.database;

public class DatabaseUsers extends SQLite {

    public DatabaseUsers(DATABASE_TYPE type) {
        super(type);
    }

    public enum COLUMNS {
        PLAYER("id", "varchar(32) NOT NULL PRIMARY KEY"),
        IGNORING_REQUEST("ignoring", "text");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

}