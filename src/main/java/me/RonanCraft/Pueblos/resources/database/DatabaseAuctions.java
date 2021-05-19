package me.RonanCraft.Pueblos.resources.database;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.selling.Auction;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseAuctions extends SQLite {

    public DatabaseAuctions() {
        super(DATABASE_TYPE.AUCTION);
    }

    public enum COLUMNS {
        AUCTION_ID("id", "integer PRIMARY KEY AUTOINCREMENT"),
        CLAIM_ID("claim_id", "integer NOT NULL"),
        PRICE("price", "integer NOT NULL"),
        HOURS("hours", "integer NOT NULL");

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<Auction> getAuctions() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Auction> list = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");

            rs = ps.executeQuery();
            while (rs.next()) {
                Auction auction = Pueblos.getInstance().getClaimHandler().getAuctionManager().loadAuction(rs);
                if (auction != null)
                    list.add(auction);
            }
            return list;
        } catch (SQLException ex) {
            Pueblos.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    public boolean deleteAuction(@NotNull Auction auction) {
        String pre = "DELETE FROM ";
        String sql = pre + table + " WHERE "
                + COLUMNS.AUCTION_ID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(auction.auctionId);
        }};
        return sqlUpdate(sql, params);
    }

    //Create/Upload an auction
    public boolean createAuction(Auction auction) {
        String pre = "INSERT INTO ";
        String sql = pre + table + " ("
                + COLUMNS.CLAIM_ID.name + ", "
                + COLUMNS.PRICE.name + ", "
                + COLUMNS.HOURS.name + ""
                + ") VALUES(?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
            add(auction.claim.claimId);
            add(auction.price);
            add(auction.time);
        }};
        return sqlCreateAuction(sql, params, auction);
    }

    //Tools
    //Saves the auction id to auction
    private boolean sqlCreateAuction(String statement, List<Object> params, Auction auction) {
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
                auction.auctionId = rs.getLong(1);
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