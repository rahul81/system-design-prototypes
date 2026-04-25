package org.prototypes.loadbalancer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkLoadBalancer {

    private static final String[] BACKEND_SERVERS = Constants.BACKEND_SERVERS;
    private static final int PORT = 9000;

    private static final AtomicInteger counter = new AtomicInteger(0);


    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("L4 Load Balancer listening on port : " + PORT);

            while (true) {
                Socket clientSocket = server.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
            }
        } catch (Exception e) {
            System.out.println("Exception while initializing server : " + e);
        }


    }

    public static void handleRequest(Socket clientSocket) {

        // Get the backend server
        int serverIdx = (counter.getAndIncrement() % BACKEND_SERVERS.length);
        System.out.println("Request Number : " + counter.get() + ", Backend index : " + serverIdx);
        String server = BACKEND_SERVERS[serverIdx];

        String[] parts = server.split(":");

        try (Socket backendSocket = new Socket(parts[0], Integer.parseInt(parts[1]))) {

            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();

            InputStream backendIn = backendSocket.getInputStream();
            OutputStream backendOut = backendSocket.getOutputStream();

            Thread transferToBackend = new Thread(() -> {
                transfer(clientIn, backendOut);
            });

            transferToBackend.start();

            transfer(backendIn, clientOut);


        } catch (IOException e) {
            System.out.println("Exception while transferring streams : " + e);
        }


    }

    public static void transfer(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Exception while data transfer: " + e);
        }
    }
}
