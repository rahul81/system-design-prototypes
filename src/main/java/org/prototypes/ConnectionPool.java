package org.prototypes;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ConnectionPool {

    private final Queue<Connection> connections = new ConcurrentLinkedDeque<>();
    private final Semaphore semaphore;
    public ConnectionPool(int numConnections) {

        this.semaphore = new Semaphore(numConnections);

        String url = "jdbc:postgresql://localhost:5432/test-db";
        String user = "postgres";
        String password = "";

        System.out.format("Creating %d connections...", numConnections);

        for (int i = 0; i < numConnections; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, user, password);

                if (conn != null) {
                    System.out.println("Successfully connected to DB, id : " + (i+1));
                    connections.offer(conn);
                } else {
                    System.out.println("Failed to connect to DB, id : " + (i+1));
                }

            } catch (SQLException e) {
                System.err.format("Exception during db connection : %s\n%s \n", e.getMessage(), e.getSQLState());
            }
        }
        System.out.println("Connections created !!!");


    }

    public void runSqlTask(int id) throws InterruptedException {



/*      // This creates new connection for every thread
        // This code fails when too many new connections are created by each new thread, db rejects new connections
        System.out.format("\nThread name: %s , Task Id : %s running ", Thread.currentThread().getName(), id + "");

        String url = "jdbc:postgresql://localhost:5432/test-db";
        String user = "postgres";
        String password = "";


        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("Successfully connected to DB ");
                Thread.sleep(100);
            } else {
                System.out.println("Failed to connect to DB");
            }
            assert conn != null;
            conn.close();


        } catch (SQLException e) {
            System.err.format("Exception during db connection : %s\n%s ", e.getMessage(), e.getSQLState());
        }
*/

        // Below code reuses connections from connection pool and does not overload the DB with new connections

        try {
            semaphore.acquire();
            Connection conn = connections.poll();
            System.out.format("\nThread name: %s , Task Id : %s running ", Thread.currentThread().getName(), id + "");
            assert conn != null;
            Statement stmt = conn.createStatement();
            stmt.executeQuery("select pg_sleep(1)");

            System.out.format("\nThread name: %s , Task Id : %s Finished ", Thread.currentThread().getName(), id + "");
            connections.offer(conn);


        }
        catch (Exception e){
            System.out.println("Exception occured during excution:" + e.getMessage());
        }
        finally {
            semaphore.release();
        }





    }

    public static void main(String[] args) {

        ExecutorService executors = Executors.newFixedThreadPool(100);

        ConnectionPool task = new ConnectionPool(20);


        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executors.submit(() -> {
                try {
                    task.runSqlTask(finalI + 1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executors.shutdown();

    }
}
