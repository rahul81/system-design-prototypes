package org.prototypes.loadbalancer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationLoadBalancer {

    private static final ArrayList<String> BACKEND_SERVERS = new ArrayList<>(List.of(Constants.BACKEND_SERVERS));
    private static final int PORT = Constants.LOAD_BALANCER_PORT;

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);


    public static class CheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "LoadBalancer is alive!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class ProxyHandler implements HttpHandler {

        public String getServer(int count) throws Exception {

            if (BACKEND_SERVERS.isEmpty()) {
                throw new Exception("No valid server available");
            }

            int serverIdx = (count % BACKEND_SERVERS.size());
            System.out.println("Request Number : " + counter.get() + ", Backend index : " + serverIdx);
            String server = BACKEND_SERVERS.get(serverIdx); // simple round-robin strategy

            try (Jedis jedis = pool.getResource()) {
                if (jedis.exists(server)) return server;
            }
            System.out.println("Removing unavailable server : " + server);
            BACKEND_SERVERS.remove(serverIdx);
            System.out.println("Updated available server lists...");
            return getServer(count);
        }

        @Override
        public void handle(HttpExchange exchange) {

            // Get the backend server
            System.out.println("Request Path : " + exchange.getRequestURI().getPath());
            try {
                String server = getServer(counter.getAndIncrement());
                URI targetUri = URI.create(server + exchange.getRequestURI());

                // Forward the request
                HttpRequest proxyRequest = HttpRequest.newBuilder()
                        .uri(targetUri)
                        .method(
                                exchange.getRequestMethod(),
                                HttpRequest.BodyPublishers.ofInputStream(exchange::getRequestBody)) // Read the input from incoming request body and forward it to backend server
                        .build();

                try {

                    HttpResponse<byte[]> response = httpClient.send(proxyRequest, HttpResponse.BodyHandlers.ofByteArray());

                    // Send response back to client
                    exchange.sendResponseHeaders(response.statusCode(), response.body().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.body());
                    }

                } catch (InterruptedException e) {
                    exchange.sendResponseHeaders(500, -1);
                    Thread.currentThread().interrupt();

                }
            } catch (Exception e) {
                System.out.println("Exception while getting available backend servers : " + e.getMessage());
            }
        }

    }

    public static void main(String[] args) throws IOException {

        // start a listener on port 9000
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);


        server.createContext("/check", new CheckHandler());
        server.createContext("/api", new ProxyHandler());

        ExecutorService executors = Executors.newFixedThreadPool(20);

        server.setExecutor(executors);
        server.start();
        System.out.println("LoadBalancer server listening on port : " + PORT);


    }
}
