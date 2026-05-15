import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseTestRunner {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");
            
            // Connect to in-memory H2 database
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            stmt = conn.createStatement();
            
            // Read and execute SQL script
            String sqlFile = "D:/market/script/test/database-test.sql";
            BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
            String line;
            StringBuilder sql = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                // Skip comment lines
                if (line.trim().startsWith("--")) {
                    continue;
                }
                sql.append(line).append("\n");
                
                // If line ends with semicolon, execute the accumulated SQL
                if (line.trim().endsWith(";")) {
                    executeStatement(stmt, sql.toString());
                    sql.setLength(0); // Clear the builder
                }
            }
            
            // Execute any remaining SQL
            if (sql.length() > 0) {
                executeStatement(stmt, sql.toString());
            }
            
            reader.close();
            System.out.println("Database test completed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) { }
        }
    }
    
    private static void executeStatement(Statement stmt, String sql) throws Exception {
        if (sql == null || sql.trim().isEmpty()) return;
        
        System.out.println("Executing: " + sql.trim());
        try {
            boolean hasResults = stmt.execute(sql);
            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                System.out.println("Results:");
                while (rs.next()) {
                    // Print all columns
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
                rs.close();
            } else {
                int updateCount = stmt.getUpdateCount();
                System.out.println("Update count: " + updateCount);
            }
        } catch (Exception e) {
            System.out.println("Error executing SQL: " + e.getMessage());
            throw e;
        }
    }
}