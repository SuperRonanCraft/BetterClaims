package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseClaims extends SQLite {

    public DatabaseClaims() {
        super(SQLite.DATABASE_TYPE.CLAIMS);
    }

    public enum COLUMNS {
        CLAIM_ID("id", "integer PRIMARY KEY AUTOINCREMENT"),
        OWNER_UUID("uuid", "varchar(32) NOT NULL"),
        OWNER_NAME("name", "varchar(32) NOT NULL"),
        POSITION("position", "text NOT NULL"),
        MEMBERS("members", "text"),
        FLAGS("flags", "text"),
        REQUESTS("requests", "text"),
        DATE("date_created", "text");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<Claim> getClaims() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Claim> list = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");

            rs = ps.executeQuery();
            while (rs.next()) {
                Claim claim = Pueblos.getInstance().getSystems().getClaimHandler().loadClaim(rs);
                if (claim != null && claim.getPosition() != null)
                    list.add(claim);
            }
            return list;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Create a claim
    public boolean createClaim(Claim claim) {
        String pre;
        if (sqlEnabled) pre = "INSERT IGNORE INTO ";
        else pre = "INSERT OR IGNORE INTO ";
        String sql = pre + table + " ("
                + COLUMNS.OWNER_UUID.name + ", "
                + COLUMNS.OWNER_NAME.name + ", "
                + COLUMNS.DATE.name + ", "
                + COLUMNS.POSITION.name + ""
                + ") VALUES(?, ?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
                //add(claim.ownerId != null ? claim.ownerId : "null");
                add(claim.ownerId != null ? claim.ownerId : "null");
                add(claim.ownerName);
                add(HelperDate.getDate(claim.dateCreated));
                add(claim.getPositionJSON());
        }};
        return sqlCreateClaim(sql, params, claim);
    }

    //Set Open/Closed Status
    public boolean updateMembers(Claim claim) {
        String sql = "UPDATE " + table + " SET " + COLUMNS.MEMBERS.name + " = ?  WHERE" + " " + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(JSONEncoding.getJsonFromMembers(claim.getMembers()));
            add(claim.claimId); }};
        return sqlUpdate(sql, params);
    }

    //Claim Saving
    void saveClaim(Claim claim) {
        String sql = "UPDATE " + table + " SET "
                + COLUMNS.POSITION.name + " = ?, "
                + COLUMNS.MEMBERS.name + " = ?, "
                + COLUMNS.REQUESTS.name + " = ?, "
                + COLUMNS.FLAGS.name + " = ? "
                + " WHERE " + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(claim.getPositionJSON());
            add(JSONEncoding.getJsonFromMembers(claim.getMembers()));
            add(JSONEncoding.getJsonFromRequests(claim.getRequests()));
            add(JSONEncoding.getJsonFromFlags(claim.getFlags().getFlags()));
            add(claim.claimId);
        }};
        claim.uploaded();
        sqlUpdate(sql, params);
    }

    public void saveChanges() {
        for (Claim claim : Pueblos.getInstance().getSystems().getClaimHandler().getClaims())
            if (claim.wasUpdated())
                saveClaim(claim);
    }

    //Tools

    private boolean sqlCreateClaim(String statement, List<Object> params, Claim claim) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            if (params != null) {
                Iterator<Object> it = params.iterator();
                int paramIndex = 1;
                while (it.hasNext()) {
                    ps.setObject(paramIndex, it.next());
                    paramIndex++;
                }
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                claim.claimId = rs.getLong(1);
            } else
                return false;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }
}