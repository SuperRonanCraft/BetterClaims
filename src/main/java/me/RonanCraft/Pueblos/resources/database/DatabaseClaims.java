package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseClaims extends SQLite {

    public DatabaseClaims() {
        super(DATABASE_TYPE.CLAIMS);
    }

    public enum COLUMNS {
        CLAIM_ID("id", "integer PRIMARY KEY AUTOINCREMENT"),
        OWNER_UUID("uuid", "varchar(32) NOT NULL"),
        OWNER_NAME("name", "varchar(32) NOT NULL"),
        POSITION("position", "text NOT NULL"),
        ADMIN_CLAIM("admin_claim", "boolean DEFAULT false"),
        MEMBERS("members", "text"),
        FLAGS("flags", "text"),
        REQUESTS("requests", "text"),
        DATE("date_created", "text"),
        PARENT("parent", "integer DEFAULT NULL");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<ClaimMain> getClaims() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<ClaimMain> claims = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");

            rs = ps.executeQuery();
            //Load all Claims
            while (rs.next()) {
                ClaimMain claim = Pueblos.getInstance().getClaimHandler().loadClaim(rs);
                if (claim != null && claim.getBoundingBox() != null)
                    claims.add(claim);
            }
            //Give child claims a parent to sleep with
            /*rs.beforeFirst(); //////// ------- LOAD CHILD CLAIMS FROM ANOTHER DATABASE!
            while (rs.next()) {
                int parent_id = rs.getInt(COLUMNS.PARENT.name);
                if (!rs.wasNull()) { //Check if the parent column was not null
                    int claim_id = rs.getInt(COLUMNS.CLAIM_ID.name);
                    ClaimChild claim_child = null;
                    for (ClaimChild claim : claims)
                        if (claim.claimId == claim_id) {
                            claim_child = claim;
                            break;
                        }
                    if (claim_child != null) {
                        for (ClaimMain claim : claims) {
                            if (claim.claimId == parent_id) {
                                claim_child.parent = claim;
                                break;
                            }
                        }
                        if (claim_child.parent == null)
                            Pueblos.getInstance().getLogger().severe("Something went wrong with claim #" + claim_id
                                    + ". It's a child claim, but a parent claim was not found for it!");
                    } else {
                        Pueblos.getInstance().getLogger().severe("Something went wrong with claim #" + claim_id + ", its a child claim but it wasn't loaded?");
                    }
                }
            }*/
            return claims;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Create a claim
    public boolean createClaim(Claim claim) {
        String pre = "INSERT INTO ";
        String sql = pre + table + " ("
                + COLUMNS.OWNER_UUID.name + ", "
                + COLUMNS.OWNER_NAME.name + ", "
                + COLUMNS.ADMIN_CLAIM.name + ", "
                + COLUMNS.DATE.name + ", "
                + COLUMNS.POSITION.name + ""
                + ") VALUES(?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
                add(getClaimOwnerID(claim));
                add(getClaimOwnerName(claim));
                add(claim.isAdminClaim());
                add(HelperDate.getDate(claim.dateCreated));
                add(claim.getBoundingBoxJSON());
        }};
        return sqlCreateClaim(sql, params, claim);
    }

    //Create a claim
    public boolean deleteClaim(ClaimMain claim) {
        String pre = "DELETE FROM ";
        String sql = pre + table + " WHERE "
                + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(claim.claimId);
        }};
        return sqlUpdate(sql, params);
    }

    //Update Members
    /*public boolean updateMembers(Claim claim) {
        String sql = "UPDATE " + table + " SET " + COLUMNS.MEMBERS.name + " = ?  WHERE" + " " + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(JSONEncoding.getJsonFromMembers(claim.getMembers()));
            add(claim.claimId); }};
        return sqlUpdate(sql, params);
    }*/

    //Claim Saving
    public boolean saveClaim(ClaimMain claim) {
        String sql = "UPDATE " + table + " SET "
                + COLUMNS.OWNER_UUID.name + " = ?,"
                + COLUMNS.OWNER_NAME.name + " = ?,"
                + COLUMNS.POSITION.name + " = ?, "
                + COLUMNS.MEMBERS.name + " = ?, "
                + COLUMNS.REQUESTS.name + " = ?, "
                + COLUMNS.FLAGS.name + " = ? "
                + " WHERE " + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(getClaimOwnerID(claim));
            add(getClaimOwnerName(claim));
            add(claim.getBoundingBoxJSON());
            add(JSONEncoding.getJsonFromMembers(claim.getMembers()));
            add(JSONEncoding.getJsonFromRequests(claim.getRequests()));
            add(JSONEncoding.getJsonFromFlags(claim.getFlags().getFlags()));
            add(claim.claimId);
        }};
        claim.uploaded();
        return sqlUpdate(sql, params);
    }

    public void saveChanges() {
        for (ClaimMain claim : Pueblos.getInstance().getClaimHandler().getMainClaims())
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

    private String getClaimOwnerID(Claim claim) {
        return claim.getOwnerID() != null ? claim.getOwnerID().toString() : "Admin Claim";
    }

    private String getClaimOwnerName(Claim claim) {
        return claim.getOwnerName() != null ? claim.getOwnerName() : "Admin Claim";
    }
}