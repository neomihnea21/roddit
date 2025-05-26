package serv;
import conn.DbConnection;
import obj.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostService {
    DbConnection db;
    private static final PostService instance = new PostService();

    private PostService() {
        db = new DbConnection(
                "jdbc:postgresql://localhost:5432/reddit_server",
                "mihnea",
                "david21silva"
        );
    }
    public static PostService getInstance() {
        return instance;
    }
    public int createPost(String message, int authorId, int subId, int postId) {
        // Explicitly inserting 0 for the score, as it's not defaulted in the DB
        String sql = "INSERT INTO reddit_app.posts (message, score, author_id, sub_id, post_id) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, message);
            stmt.setInt(2, 0); // Setting score to 0
            stmt.setInt(3, authorId);
            stmt.setInt(4, subId);
            stmt.setInt(5, postId);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt("post_id");
                        System.out.println("New post successfully inserted!");
                        return generatedId;
                    }
                }
            }
            return 0;//if we get all the way here, we didn't insert

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public void readPosts() {
        String sql = "SELECT * FROM reddit_app.posts";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Post ID: " + rs.getInt("post_id") +
                                   ", Message: " + rs.getString("message") +
                                   ", Score: " + rs.getInt("score") +
                                   ", Author ID: " + rs.getInt("author_id") +
                                   ", Subroddit ID: " + rs.getInt("sub_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean updatePostMessage(int postId, String newMessage) {
        String sql = "UPDATE reddit_app.posts SET message = ? WHERE post_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newMessage);
            stmt.setInt(2, postId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updatePostScore(int postId, int newScore) {
        String sql = "UPDATE reddit_app.posts SET score = ? WHERE post_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setInt(2, postId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deletePostById(int postId) {
        String sql = "DELETE FROM reddit_app.posts WHERE post_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}