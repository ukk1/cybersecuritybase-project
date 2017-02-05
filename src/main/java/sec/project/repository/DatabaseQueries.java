/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.repository;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.h2.tools.RunScript;
import sec.project.domain.Signup;
/**
 *
 * @author ukk1
 */
public class DatabaseQueries {  
    Connection connection = null;
    private String databaseAddress = "jdbc:h2:file:./database";
    
    public DatabaseQueries() {
        try {
           this.connection = DriverManager.getConnection(databaseAddress, "sa", "");
            RunScript.execute(connection, new FileReader("src/main/resources/sql/schema.sql"));
            RunScript.execute(connection, new FileReader("src/main/resources/sql/data.sql"));
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }
    
    //retrieve user information from database
    public Signup getAccount(String username, String password) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT id, username, password FROM accounts WHERE username='" +username+ "' AND password='" +password+ "'");
        while (resultSet.next()) {
            return new Signup(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"));
    }
        return null;
    }
}
   