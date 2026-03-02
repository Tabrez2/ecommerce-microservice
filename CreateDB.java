import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDB {
    static {
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres?options=-c%20timezone=UTC";
        String user = "tabrez";
        String password = "tabrez";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            createDatabase(stmt, "payment");
            createDatabase(stmt, "order");
            createDatabase(stmt, "product");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createDatabase(Statement stmt, String dbName) throws Exception {
        try {
            stmt.execute("CREATE DATABASE \"" + dbName + "\"");
            System.out.println("Database '" + dbName + "' created successfully.");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Database '" + dbName + "' already exists.");
            } else {
                throw e;
            }
        }
    }
}