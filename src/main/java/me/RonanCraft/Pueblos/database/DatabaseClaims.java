package me.RonanCraft.Pueblos.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.claims.ClaimData;
import me.RonanCraft.Pueblos.claims.Claim_Child;
import me.RonanCraft.Pueblos.claims.Claim;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.resources.helper.HelperClaim;
import me.RonanCraft.Pueblos.resources.helper.HelperDate;
import me.RonanCraft.Pueblos.resources.helper.HelperJSON;

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

    public HashMap<CLAIM_TYPE, List<ClaimData>> getClaims() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<ClaimData> claimDataMains = new ArrayList<>();
            List<ClaimData> claimDataChildren = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE (" + COLUMNS.PARENT + " IS NULL OR " + COLUMNS.PARENT + " = -1)");

            rs = ps.executeQuery();
            //Load all Claims
            while (rs.next()) {
                ClaimData claimData = HelperClaim.loadClaim(rs, CLAIM_TYPE.PARENT, null);
                if (claimData != null && claimData.getBoundingBox() != null)
                    claimDataMains.add(claimData);
            }
            rs.close();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE (" + COLUMNS.PARENT + " IS NOT NULL AND " + COLUMNS.PARENT + " IS NOT -1)");
            rs = ps.executeQuery();

            while (rs.next()) {
                ClaimData claimData = HelperClaim.loadClaim(rs, CLAIM_TYPE.CHILD, claimDataMains);
                if (claimData != null && claimData.getBoundingBox() != null)
                    claimDataChildren.add(claimData);
            }
            //Organize claims
            HashMap<CLAIM_TYPE, List<ClaimData>> hash = new HashMap<>();
            hash.put(CLAIM_TYPE.PARENT, claimDataMains);
            hash.put(CLAIM_TYPE.CHILD, claimDataChildren);
            return hash;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Create a claim
    public boolean createClaim(ClaimData claimData) {
        String pre = "INSERT INTO ";
        String sql = pre + table + " ("
                + COLUMNS.OWNER_UUID.name + ", "
                + COLUMNS.OWNER_NAME.name + ", "
                + COLUMNS.ADMIN_CLAIM.name + ", "
                + COLUMNS.DATE.name + ", "
                + COLUMNS.POSITION.name + ", "
                + COLUMNS.PARENT.name + ""
                + ") VALUES(?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(getClaimOwnerID(claimData));
                add(getClaimOwnerName(claimData));
                add(claimData.isAdminClaim());
                add(HelperDate.getDate(claimData.dateCreated));
                add(getBoundingBoxJSON(claimData));
                add(getParent(claimData));
        }};
        return sqlCreateClaim(sql, params, claimData);
    }

    //Delete a main claim
    public boolean deleteClaim(Claim claim, List<Claim_Child> children) {
        String pre = "DELETE FROM ";
        String sql = pre + table + " WHERE "
                + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(claim.claimId);
        }};
        if (sqlUpdate(sql, params)) {
            for (Claim_Child claimChild : children) {
                sql = pre + table + " WHERE "
                        + COLUMNS.CLAIM_ID.name + " = ?";
                params = new ArrayList<Object>() {{
                    add(claimChild.claimId);
                }};
                sqlUpdate(sql, params);
            }
        } else
            return false;
        return true;
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
    public boolean saveClaim(ClaimData claimData) {
        String sql = "UPDATE " + table + " SET "
                + COLUMNS.OWNER_UUID.name + " = ?,"
                + COLUMNS.OWNER_NAME.name + " = ?,"
                + COLUMNS.POSITION.name + " = ?, "
                + COLUMNS.MEMBERS.name + " = ?, "
                + COLUMNS.REQUESTS.name + " = ?, "
                + COLUMNS.FLAGS.name + " = ? "
                + " WHERE " + COLUMNS.CLAIM_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(getClaimOwnerID(claimData));
            add(getClaimOwnerName(claimData));
            add(getBoundingBoxJSON(claimData));
            add(HelperJSON.getJsonFromMembers(claimData.getMembers()));
            add(HelperJSON.getJsonFromRequests(claimData.getRequests()));
            add(HelperJSON.getJsonFromFlags(claimData.getFlags().getFlags()));
            add(claimData.claimId);
        }};
        claimData.uploaded();
        return sqlUpdate(sql, params);
    }

    public void saveChanges() {
        for (ClaimData claimData : Pueblos.getInstance().getClaimHandler().getClaimsAll())
            if (claimData.wasUpdated())
                saveClaim(claimData);
    }

    //Tools

    private boolean sqlCreateClaim(String statement, List<Object> params, ClaimData claimData) {
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
                claimData.claimId = rs.getLong(1);
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
    private String getClaimOwnerID(ClaimData claimData) {
        return claimData.getOwnerID() != null ? claimData.getOwnerID().toString() : "Admin Claim";
    }

    private String getClaimOwnerName(ClaimData claimData) {
        return claimData.getOwnerName() != null ? claimData.getOwnerName() : "Admin Claim";
    }

    private long getParent(ClaimData claimData) {
        if (claimData instanceof Claim_Child)
            return ((Claim_Child) claimData).getParent().claimId;
        return -1;
    }

    public String getBoundingBoxJSON(ClaimData claimData) {
        return HelperJSON.getJsonFromBoundingBox(claimData.getBoundingBox());
    }
}