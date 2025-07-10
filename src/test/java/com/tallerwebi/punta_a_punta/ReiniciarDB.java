package com.tallerwebi.punta_a_punta;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ReiniciarDB {

    private static DataSource dataSource;

    // Initialize the data source
    static {
        initializeDataSource();
    }

    private static void initializeDataSource() {
        String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
        String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
        String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
        String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
        String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "user";

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                dbHost, dbPort, dbName));
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);

        dataSource = ds;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // OPTION 1: Use your reset-db.sql file (RECOMMENDED)
    // This uses TRUNCATE - fast, efficient, keeps database structure
    public static void limpiarBaseDeDatos() {
        try (Connection connection = dataSource.getConnection()) {
            // Execute the reset-db.sql script (which uses TRUNCATE statements)
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("reset-db.sql"));
            System.out.println("Base de datos limpiada exitosamente usando TRUNCATE");
        } catch (SQLException e) {
            System.err.println("Error al limpiar la base de datos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database cleanup failed", e);
        }
    }

}