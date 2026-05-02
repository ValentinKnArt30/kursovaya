package ru.netology.data;

import lombok.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class SQL {

    private static final QueryRunner runner = new QueryRunner();

    private static String url;
    private static String user;
    private static String password;

    static {
        try {

            String currentDir = System.getProperty("user.dir");
            File configFile = new File(currentDir, "application.properties");

            if (!configFile.exists()) {
                throw new RuntimeException("application.properties not found in project root: " + configFile.getAbsolutePath());
            }

            Properties props = new Properties();
            try (InputStream is = new FileInputStream(configFile)) {
                props.load(is);
            }

            url = System.getProperty(
                    "db.url",
                    props.getProperty("spring.datasource.url")
            );

            user = System.getProperty(
                    "db.user",
                    props.getProperty("spring.datasource.username")
            );

            password = System.getProperty(
                    "db.password",
                    props.getProperty("spring.datasource.password")
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB properties from project root", e);
        }
    }

    private static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

    @SneakyThrows
    public static void clear() {
        try (Connection conn = getConnection()) {
            runner.update(conn, "DELETE FROM payment_entity;");
            runner.update(conn, "DELETE FROM credit_request_entity;");
            runner.update(conn, "DELETE FROM order_entity;");
        }
    }

    @SneakyThrows
    public static String getStatusPaymentCard() {
        String sql = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static long getNumberPaymentCard() {
        String sql = "SELECT COUNT(transaction_id) FROM payment_entity";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static int getAmount() {
        String sql = "SELECT amount FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (Connection conn = getConnection()) {
            String amount = runner.query(conn, sql, new ScalarHandler<>()).toString();
            return Integer.parseInt(amount);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        private String id;
        private int amount;
        private Timestamp created;
        private String status;
        private String transaction_id;
    }

    @SneakyThrows
    public static List<PaymentEntity> getPayments() {
        String sql = "SELECT * FROM payment_entity ORDER BY created DESC";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new BeanListHandler<>(PaymentEntity.class));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private Timestamp created;
        private String status;
    }

    @SneakyThrows
    public static List<CreditRequestEntity> getCreditsRequest() {
        String sql = "SELECT * FROM credit_request_entity ORDER BY created DESC";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new BeanListHandler<>(CreditRequestEntity.class));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        private String id;
        private Timestamp created;
        private String credit_id;
        private String payment_id;
    }

    @SneakyThrows
    public static List<OrderEntity> getOrders() {
        String sql = "SELECT * FROM order_entity ORDER BY created DESC";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new BeanListHandler<>(OrderEntity.class));
        }
    }
}