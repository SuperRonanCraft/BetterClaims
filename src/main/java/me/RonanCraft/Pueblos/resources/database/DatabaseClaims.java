package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimChild;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        PARENT("parent", "integer DEFAULT -1");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public HashMap<CLAIM_TYPE, List<Claim>> getClaims() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Claim> claimMains = new ArrayList<>();
            List<Claim> claimChildren = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE (" + COLUMNS.PARENT + " IS NULL OR " + COLUMNS.PARENT + " = -1)");

            rs = ps.executeQuery();
            //Load all Claims
            while (rs.next()) {
                Claim claim = HelperClaim.loadClaim(rs, CLAIM_TYPE.MAIN, null);
                if (claim != null && claim.getBoundingBox() != null)
                    claimMains.add(claim);
            }
            rs.close();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE (" + COLUMNS.PARENT + " IS NOT NULL AND " + COLUMNS.PARENT + " IS NOT -1)");
            rs = ps.executeQuery();

            while (rs.next()) {
                Claim claim = HelperClaim.loadClaim(rs, CLAIM_TYPE.CHILD, claimMains);
                if (claim != null && claim.getBoundingBox() != null)
                    claimChildren.add(claim);
            }
            //Organize claims
            HashMap<CLAIM_TYPE, List<Claim>> hash = new HashMap<>();
            hash.put(CLAIM_TYPE.MAIN, claimMains);
            hash.put(CLAIM_TYPE.CHILD, claimChildren);
            return hash;
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
                + COLUMNS.POSITION.name + ", "
                + COLUMNS.PARENT.name + ""
                + ") VALUES(?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
                add(getClaimOwnerID(claim));
                add(getClaimOwnerName(claim));
                add(claim.isAdminClaim());
                add(HelperDate.getDate(claim.dateCreated));
                add(getBoundingBoxJSON(claim));
                add(getParent(claim));
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
    public boolean saveClaim(Claim claim) {
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
            add(getBoundingBoxJSON(claim));
            add(JSONEncoding.getJsonFromMembers(claim.getMembers()));
            add(JSONEncoding.getJsonFromRequests(claim.getRequests()));
            add(JSONEncoding.getJsonFromFlags(claim.getFlags().getFlags()));
            add(claim.claimId);
        }};
        claim.uploaded();
        return sqlUpdate(sql, params);
    }

    public void saveChanges() {
        for (Claim claim : Pueblos.getInstance().getClaimHandler().getClaimsAll())
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

    //Tools
    private String getClaimOwnerID(Claim claim) {
        return claim.getOwnerID() != null ? claim.getOwnerID().toString() : "Admin Claim";
    }

    private String getClaimOwnerName(Claim claim) {
        return claim.getOwnerName() != null ? claim.getOwnerName() : "Admin Claim";
    }

    private long getParent(Claim claim) {
        if (claim instanceof ClaimChild)
            return ((ClaimChild) claim).getParent().claimId;
        return -1;
    }

    public String getBoundingBoxJSON(Claim claim) {
        return JSONEncoding.getJsonFromBoundingBox(claim.getBoundingBox());
    }
}