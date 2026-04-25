package org.example.database;

import org.flywaydb.core.Flyway;

public class FlywayConfig {

    public static void rodarMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/BAR&RESTAURANTE",
                        "postgres",
                        "1234"
                )
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        System.out.println("Migrations executadas!");
    }
}
