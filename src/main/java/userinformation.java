import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class userinformation extends L4STImpl {
    protected userinformation(Connection connection) throws Exception {
        super(connection);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // connect way #1
            String url1 = "jdbc:mysql://localhost:3306";
            String user = "root";
            String password = "Medodragon17#";

            Connection conn1 = DriverManager.getConnection(url1, user, password);
            if (conn1 != null) {
                System.out.println("Connected to the database test1");
            }
            userinformation us = new userinformation(conn1);
        } catch (Exception ex) {
            System.out.println("An error occurred.");
            ex.printStackTrace();
        }
    }
}
