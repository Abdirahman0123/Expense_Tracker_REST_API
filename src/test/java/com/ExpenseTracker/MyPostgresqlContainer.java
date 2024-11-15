package com.ExpenseTracker;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class MyPostgresqlContainer extends PostgreSQLContainer<MyPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static MyPostgresqlContainer container;

    private MyPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static MyPostgresqlContainer getInstance() {
        if (container == null) {
            container = new MyPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}