package serv;
import conn.DbConnection;
import obj.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class UserService{
    DbConnection db;
    private static final UserService instance = new UserService();
    private UserService(){
        db=new DbConnection(
           "jdbc:postgresql://localhost:5432/reddit_server",
           "mihnea",
           "david21silva"
        );
    }///todo make it use parameters
    public static UserService getInstance(){
        return instance;
    }
    public void createUser(int user_id, String name, String password) {
        String sql = "INSERT INTO reddit_app.users (user_id, name, password) VALUES (?, ?, ?);";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, name);
            stmt.setString(3, password);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getUserIdByName(String name) {
        String sql = "SELECT user_id FROM reddit_app.users WHERE username = ?";
        int userId = -1; // Default to -1 if not found
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name); // Set the title parameter
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id"); // Retrieve the ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
    public void readUsers(){
        String sql="SELECT * FROM reddit_app.users";
        try{
            Connection conn=db.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("username"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public boolean updateUsername(String currentName, String newName) {
        String sql = "UPDATE reddit_app.users SET username = ? WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, currentName);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteUserByUsername(String username) {
        String sql = "DELETE FROM reddit_app.users WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}