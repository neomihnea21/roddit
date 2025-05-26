package serv;
import conn.DbConnection;
import obj.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class SubrodditService {
    DbConnection db;
    private static final SubrodditService instance = new SubrodditService();

    private SubrodditService() {
        db = new DbConnection(
                "jdbc:postgresql://localhost:5432/reddit_server",
                "mihnea",
                "david21silva"
        );
    } // todo make it use parameters
    public static SubrodditService getInstance() {
        return instance;
    }
    public void createSubroddit(String title) {
        String sql = "INSERT INTO reddit_app.subroddits (title) VALUES (?);";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new subroddit was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void readSubroddits() {
        String sql = "SELECT * FROM reddit_app.subroddits";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("sub_id") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSubrodditIdByTitle(String title) {
        String sql = "SELECT sub_id FROM reddit_app.subroddits WHERE title = ?";
        int subrodditId = -1; // Default to -1 if not found
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title); // Set the title parameter
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subrodditId = rs.getInt("sub_id"); // Retrieve the ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subrodditId;
    }
    public boolean updateSubrodditTitle(String oldTitle, String newTitle) {
        String sql = "UPDATE reddit_app.subroddits SET title = ? WHERE title = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newTitle);
            stmt.setString(2, oldTitle);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSubrodditById(String name) {
        String sql = "DELETE FROM reddit_app.subroddits WHERE title = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}