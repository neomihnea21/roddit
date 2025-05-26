package conn;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DbConnection{
    String url, username, password;
    public DbConnection(String url, String username, String password){
        this.username=username;
        this.password=password;
        this.url=url;
    }
    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    } 
}