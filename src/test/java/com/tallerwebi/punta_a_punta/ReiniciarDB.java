package com.tallerwebi.punta_a_punta;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ReiniciarDB {

    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;

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
        jdbcTemplate = new JdbcTemplate(dataSource);
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

    // OPTION 2: Manual cleanup + data loading (if you prefer more control)
    public static void limpiarBaseDeDatosManual() {
        try {
            // Disable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Clean all tables in reverse dependency order
            jdbcTemplate.execute("TRUNCATE TABLE evento");
            jdbcTemplate.execute("TRUNCATE TABLE Comentario");
            jdbcTemplate.execute("TRUNCATE TABLE Publicacion");
            jdbcTemplate.execute("TRUNCATE TABLE usuario_materia");
            jdbcTemplate.execute("TRUNCATE TABLE Materia");
            jdbcTemplate.execute("TRUNCATE TABLE Usuario");
            jdbcTemplate.execute("TRUNCATE TABLE Carrera");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            // Load initial data
            cargarDatosIniciales();

            System.out.println("Base de datos limpiada exitosamente (manual)");
        } catch (Exception e) {
            System.err.println("Error al limpiar la base de datos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database cleanup failed", e);
        }
    }

    // OPTION 3: Keep Docker approach but use reset-db.sql file
    public static void limpiarBaseDeDatosConDocker() {
        try {
            String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
            String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
            String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
            String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
            String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "user";

            // Load the reset-db.sql file content
            String sqlScript = loadResetDbScript();

            String comando = String.format(
                    "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
                    dbHost, dbPort, dbUser, dbPassword, dbName, sqlScript
            );

            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", comando});
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Base de datos limpiada exitosamente con Docker");
            } else {
                System.err.println("Error al limpiar la base de datos. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error ejecutando script de limpieza: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to load initial data
    private static void cargarDatosIniciales() {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
            System.out.println("Datos iniciales cargados exitosamente");
        } catch (SQLException e) {
            System.err.println("Error cargando datos iniciales: " + e.getMessage());
            throw new RuntimeException("Initial data loading failed", e);
        }
    }

    // Helper method to load reset-db.sql content as string (for Docker option)
    private static String loadResetDbScript() {
        try {
            ClassPathResource resource = new ClassPathResource("reset-db.sql");
            return new String(resource.getInputStream().readAllBytes())
                    .replace("\"", "\\\"")  // Escape quotes for shell
                    .replace("\n", " ");    // Remove newlines for shell
        } catch (IOException e) {
            throw new RuntimeException("Failed to load reset-db.sql", e);
        }
    }
}