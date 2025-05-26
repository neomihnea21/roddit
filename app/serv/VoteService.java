package serv;
import conn.DbConnection;
import obj.User;
import obj.Vote;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VoteService {
    DbConnection db;
    private static final VoteService instance = new VoteService();
    private VoteService() {
        db = new DbConnection(
                "jdbc:postgresql://localhost:5432/reddit_server",
                "mihnea",
                "david21silva"
        );
    }
    public static VoteService getInstance() {
        return instance;
    }
    public enum VoteType {
        UPVOTE(1),
        DOWNVOTE(-1);
        private final int scoreValue;
        VoteType(int scoreValue) {
            this.scoreValue = scoreValue;
        }
        public int getScoreValue() {
            return scoreValue;
        }
    }
    public boolean createVote(int score, int userId, int postId) {
        // fire alarm: you can't give a vote not worth +-1
        if (score != VoteType.UPVOTE.getScoreValue() && score != VoteType.DOWNVOTE.getScoreValue()) {
            System.err.println("Invalid vote score. Must be +1 or -1.");
            return false;
        }
        String sql = "INSERT INTO reddit_app.votes (value, id_voter, id_post) VALUES (?, ?, ?);";
        try (Connection conn = db.getConnection();
             // we won't need Statement.RETURN_GENERATED_KEYS if we don't plan to refer to vote_id later
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setInt(2, userId);
            stmt.setInt(3, postId);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getTotalScoreForPost(int postId) {
        String sql = "SELECT COALESCE(SUM(value), 0) AS total_score FROM reddit_app.votes WHERE id_post = ?";
        int totalScore = 0; // default to 0
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalScore = rs.getInt("total_score"); // Get the sum
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalScore;
    }
    public boolean updateVoteScore(int voteId, int newScore) {
        if (newScore != VoteType.UPVOTE.getScoreValue() && newScore != VoteType.DOWNVOTE.getScoreValue()) {
            System.err.println("Invalid vote score for update. Must be +1 or -1.");
            return false;
        }
        String sql = "UPDATE reddit_app.votes SET value = ? WHERE vote_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setInt(2, voteId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteVote(int voteId) {
        String sql = "DELETE FROM reddit_app.votes WHERE vote_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voteId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteUserVoteOnPost(int userId, int postId) {
        String sql = "DELETE FROM reddit_app.votes WHERE id_user = ? AND id_post = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}