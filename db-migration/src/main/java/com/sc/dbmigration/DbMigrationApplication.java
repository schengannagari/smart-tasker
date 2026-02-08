package com.sc.dbmigration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class DbMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbMigrationApplication.class, args);
    }

    @Bean
    public ApplicationRunner runMigration(DataSource datasource) {
        return args -> {
            System.out.println("Database Checking....");
            System.out.println(datasource.getClass().getSimpleName());
            Flyway flyway = Flyway.configure()
                    .dataSource(datasource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            System.out.println("Flyway migration completed successfully....");
        };
    }
}
