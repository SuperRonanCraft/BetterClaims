package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class Database {

    boolean sqlEnabled;
    Connection connection;
    public String table_ticket = "AMR_Data";
    public String table_nextid = "AMR_NextID";

    private final String column_nextTicketID = "NextTicketID";

    public enum COLUMNS { //FIX COLUMNS
        TICKET_ID("TicketID", "int PRIMARY KEY"),
        UUID("uuid", "varchar(32)"),
        AUTHOR("name", "varchar(16)"),
        MESSAGE("msg", "text"),
        OPEN("open", "boolean DEFAULT true"),
        //Integers
        X("x", "int"),
        Y("y", "int"),
        Z("z", "int"),
        IMPORTANCE("importance", "int"),
        RATING("rating", "int"),
        //Strings
        WORLD("world", "text"),
        TIME("time", "text"),
        CATEGORY("category", "text"),
        REPLY("reply", "text"),
        REPLIER("replier", "text"),
        RESOLVED("resolved", "text DEFAULT null"),
        CLAIMED_BY("claimedBy", "text DEFAULT null"),
        SERVER("server", "text DEFAULT null"),
        //Booleans
        FLAGGED("flagged", "boolean"),
        BROADCAST("broadcast", "boolean DEFAULT false");

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
            ps = conn.prepareStatement("SELECT * FROM " + table_ticket + " WHERE " + COLUMNS.TICKET_ID.name + " = 0");

            rs = ps.executeQuery();
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        } finally {
            close(ps, rs, conn);
        }
    }

    public void setNextId(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT " + column_nextTicketID + " FROM " + table_nextid);

            rs = ps.executeQuery();
            if (rs.next()) {
                int _id = rs.getInt(1);
                ps = conn.prepareStatement("UPDATE " + table_nextid + " SET " + column_nextTicketID + " = " + (id) + " WHERE " + column_nextTicketID + " = " + _id);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
    }

    public int getNextId() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT " + column_nextTicketID + " FROM " + table_nextid);

            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                // nextTicketID
                ps = conn.prepareStatement("UPDATE " + table_nextid + " SET " + column_nextTicketID + " = " + (id + 1) + " WHERE " + column_nextTicketID + " = " + id);
                ps.executeUpdate();
                return id;
            }
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return -1;
    }

    /*public List<Ticket> getTickets() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Ticket> list = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table_ticket + ";");

            rs = ps.executeQuery();
            while (rs.next()) {
                Ticket ticket = AdvancedModreq.getInstance().getSystems().getTicket().loadTicket(rs);
                list.add(ticket);
            }
            return list;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Create a ticket
    public boolean createTicket(Ticket ticket) {
        String pre;
        if (sqlEnabled) pre = "INSERT IGNORE INTO ";
        else pre = "INSERT OR IGNORE INTO ";
        String sql = pre + table_ticket + " ("
                + COLUMNS.TICKET_ID.name + ", "
                + COLUMNS.UUID.name + ", "
                + COLUMNS.AUTHOR.name + ", "
                + COLUMNS.MESSAGE.name + ", "
                + COLUMNS.OPEN.name + ", "
                //Ints
                + COLUMNS.X.name + ", "
                + COLUMNS.Y.name + ", "
                + COLUMNS.Z.name + ", "
                + COLUMNS.IMPORTANCE.name + ", "
                + COLUMNS.RATING.name + ", "
                //Strings
                + COLUMNS.WORLD.name + ", "
                + COLUMNS.TIME.name + ", "
                + COLUMNS.CATEGORY.name + ", "
                + COLUMNS.REPLY.name + ", "
                + COLUMNS.REPLIER.name + ", "
                + COLUMNS.RESOLVED.name + ", "
                + COLUMNS.CLAIMED_BY.name + ", "
                + COLUMNS.SERVER.name + ", "
                //Booleans
                + COLUMNS.FLAGGED.name + ", "
                + COLUMNS.BROADCAST.name
                + ") VALUES(?, ?, ?, ?, ?, " +
                //Ints
                "?, ?, ?, ?, ?, " +
                //Strings
                "?, ?, ?, ?, ?, ?, ?, ?, " +
                //Booleans
                "?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(ticket.getId());
                add(ticket.getUUIDs());
                add(ticket.getAuthorName());
                add(ticket.getMsg());
                add(ticket.getOpen());
                //Ints
                add(ticket.getX());
                add(ticket.getY());
                add(ticket.getZ());
                add(ticket.getImportance());
                add(ticket.getRating());
                //Strings
                add(ticket.getWorld());
                add(ticket.getTime());
                add(ticket.getCategory());
                add(JSONEncoding.getJsonFromList(COLUMNS.REPLY.name, ticket.getReply()));
                add(JSONEncoding.getJsonFromList(COLUMNS.REPLIER.name, ticket.getReplier()));
                add(ticket.getResolved());
                add(ticket.getClaimedBy());
                add(ticket.getServer());
                //Booleans
                add(ticket.getFlagged());
                add(ticket.getBroadcast());
        }};
        return sqlUpdate(sql, params);
    }

    //Set Open/Closed Status
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }*/

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