package org.prototypes.resourcemanager;

import org.prototypes.loadbalancer.Constants;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HealthCheck {

    private static final String[] servers = Constants.BACKEND_SERVERS;
    // Point health check service to servers to monitor (Initial Values)
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String uriPath = "/api/check";

    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

    private static void checkServer(String target) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target + uriPath))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.setex(target, 5, "true");
                } catch (Exception e) {
                    System.out.println("Exception while connecting to Redis server : " + e.getMessage());
                }
            }

            System.out.format("Target Server : %s Response status: %d \n Response : %s%n", target, response.statusCode(), response.body());

        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
            System.out.println("Health Check Exception : " + e);
        }

    }

    public static void main(String[] args) {

        try (Jedis jedis = pool.getResource()) {
            for (String server : servers) {
                jedis.setex(server, 5, "true");
            }
        } catch (Exception e) {
            System.out.println("Exception while connecting to Redis server : " + e.getMessage());
        }

        System.out.println("Health Check monitor started!");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(servers.length);
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
        for (String server : servers) {
            scheduler.scheduleAtFixedRate(() -> checkServer(server), 0, 5, TimeUnit.SECONDS);
        }
    }

}
