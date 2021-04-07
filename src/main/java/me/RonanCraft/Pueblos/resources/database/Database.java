package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public abstract class Database {

    boolean sqlEnabled;
    Connection connection;
    public String table;

    public enum COLUMNS {
        CLAIM_ID("id", "int AUTO_INCREMENT PRIMARY KEY"),
        OWNER_UUID("uuid", "varchar(32)"),
        OWNER_NAME("name", "varchar(32)"),
        //Integers
        //X("x", "int"),
        //Y("y", "int"),
        //Z("z", "int"),
        //IMPORTANCE("importance", "int"),
        //RATING("rating", "int"),
        //Strings
        //WORLD("world", "text"),
        //TIME("time", "text"),
        //CATEGORY("category", "text"),
        POSITION("position", "text");
        //REPLIER("replier", "text"),
        //RESOLVED("resolved", "text DEFAULT null"),
        //CLAIMED_BY("claimedBy", "text DEFAULT null"),
        //SERVER("server", "text DEFAULT null"),
        //Booleans
        //FLAGGED("flagged", "boolean"),
        //BROADCAST("broadcast", "boolean DEFAULT false");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() { //Let in console know if its all setup or not
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + COLUMNS.OWNER_UUID.name + " = 0");

            rs = ps.executeQuery();
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        } finally {
            close(ps, rs, conn);
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

    //Create a ticket
    public boolean createClaim(Claim claim) {
        String pre;
        if (sqlEnabled) pre = "INSERT IGNORE INTO ";
        else pre = "INSERT OR IGNORE INTO ";
        String sql = pre + table + " ("
                //+ COLUMNS.CLAIM_ID.name + ", "
                + COLUMNS.OWNER_UUID.name + ", "
                + COLUMNS.OWNER_NAME.name + ", "
                /*+ COLUMNS.AUTHOR.name + ", "
                + COLUMNS.MESSAGE.name + ", "
                + COLUMNS.OPEN.name + ", "
                //Ints
                + COLUMNS.X.name + ", "
                + COLUMNS.Y.name + ", "
                + COLUMNS.Z.name + ", "
                + COLUMNS.IMPORTANCE.name + ", "
                + COLUMNS.RATING.name + ", "*/
                //Strings
                + COLUMNS.POSITION.name + ""
                //+ COLUMNS.TIME.name + ", "
                /*+ COLUMNS.CATEGORY.name + ", "
                + COLUMNS.REPLY.name + ", "
                + COLUMNS.REPLIER.name + ", "
                + COLUMNS.RESOLVED.name + ", "
                + COLUMNS.CLAIMED_BY.name + ", "
                + COLUMNS.SERVER.name + ", "
                //Booleans
                + COLUMNS.FLAGGED.name + ", "
                + COLUMNS.BROADCAST.name*/
                + ") VALUES(?, ?, ?, ?)";/*, ?, " +
                //Ints
                "?, ?, ?, ?, ?, " +
                //Strings
                "?, ?, ?, ?, ?, ?, ?, ?, " +
                //Booleans
                "?, ?)";*/
        List<Object> params = new ArrayList<>() {{
                //add(claim.ownerId != null ? claim.ownerId : "null");
                add(claim.ownerId != null ? claim.ownerId : "null");
                add(claim.ownerName);
                /*add(ticket.getAuthorName());
                add(ticket.getMsg());
                add(ticket.getOpen());
                //Ints
                add(ticket.getX());
                add(ticket.getY());
                add(ticket.getZ());
                add(ticket.getImportance());
                add(ticket.getRating());
                //Strings*/
                //add(claim.p.getName());
                add(claim.getPositionJSON());
                /*add(ticket.getTime());
                add(ticket.getCategory());
                add(JSONEncoding.getJsonFromList(COLUMNS.REPLY.name, ticket.getReply()));
                add(JSONEncoding.getJsonFromList(COLUMNS.REPLIER.name, ticket.getReplier()));
                add(ticket.getResolved());
                add(ticket.getClaimedBy());
                add(ticket.getServer());
                //Booleans
                add(ticket.getFlagged());
                add(ticket.getBroadcast());*/
        }};
        return sqlUpdate(sql, params);
    }

    /*//Set Open/Closed Status
    public boolean setStatus(Ticket ticket, boolean open) {
        String sql = "UPDATE " + table_ticket + " SET " + COLUMNS.OPEN.name + " = ?  WHERE" + " " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(open);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set the Imporance
    public boolean setImportance(Ticket ticket, int importance) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.IMPORTANCE.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(importance);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set player who has ticket claimed (uuid of player)
    public boolean setClaimedBy(Ticket ticket, String uuid) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.CLAIMED_BY.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(uuid);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set the list of replies and repliers
    public boolean setReplies(Ticket ticket, List<String> replies, List<String> repliers) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.REPLY.name + " = ?, " + COLUMNS.REPLIER.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(JSONEncoding.getJsonFromList(COLUMNS.REPLY.name, replies));
            add(JSONEncoding.getJsonFromList(COLUMNS.REPLIER.name, repliers));
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set how well the ticket was handled
    public boolean setRating(Ticket ticket, int rating) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.RATING.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(rating);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set if ticket is flagged really important
    public boolean setFlag(Ticket ticket, boolean flag) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.FLAGGED.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(flag);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set the category of ticket
    public boolean setCategory(Ticket ticket, String category) {
        String sql = "UPDATE " + table_ticket + " SET "
                + COLUMNS.CATEGORY.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(category);
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }

    //Set the broadcast
    public boolean setBroadcast(List<Ticket> tickets, boolean broadcast) {
        List<String> statements = new ArrayList<>();
        List<List<Object>> params1 = new ArrayList<>();
        for (Ticket ticket : tickets) {
            String sql = "UPDATE " + table_ticket + " SET "
                    + COLUMNS.BROADCAST.name + " = ? WHERE " + COLUMNS.TICKET_ID.name + " = ?";
            List<Object> params = new ArrayList<Object>() {{
                add(broadcast);
                add(ticket.getId());
            }};
            statements.add(sql);
            params1.add(params);
        }
        return sqlUpdate(statements, params1);
    }

    //Purging
    public boolean purgeStatus(boolean status) {
        String sql = "DELETE FROM " + table_ticket + " WHERE " + COLUMNS.OPEN.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(status); }};
        return sqlUpdate(sql, params);
    }

    public boolean purgeAll() {
        String sql = "DELETE FROM " + table_ticket;
        return sqlUpdate(sql, null);
    }

    public boolean deleteTicket(Ticket ticket) {
        String sql = "DELETE FROM " + table_ticket + " WHERE " + COLUMNS.TICKET_ID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(ticket.getId()); }};
        return sqlUpdate(sql, params);
    }
*/
    //Tools
    private boolean sqlUpdate(String statement, List<Object> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
            if (params != null) {
                Iterator<Object> it = params.iterator();
                int paramIndex = 1;
                while (it.hasNext()) {
                    ps.setObject(paramIndex, it.next());
                    paramIndex++;
                }
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }

    private boolean sqlCreateClaim(String statement, List<Object> params, Claim claim) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
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
                claim.claimId = rs.getLong(COLUMNS.CLAIM_ID.name);
            }
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }

    //Tools
    private boolean sqlUpdate(List<String> statement1, List<List<Object>> params1) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            for (int i = 0; i < statement1.size(); i++) {
                String statement = statement1.get(i);
                List<Object> params = params1.get(i);
                ps = conn.prepareStatement(statement);
                if (params != null) {
                    Iterator<Object> it = params.iterator();
                    int paramIndex = 1;
                    while (it.hasNext()) {
                        ps.setObject(paramIndex, it.next());
                        paramIndex++;
                    }
                }
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }

    //Processing
    private void close(PreparedStatement ps, ResultSet rs, Connection conn) {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            Error.close(Pueblos.getInstance(), ex);
        }
    }
}