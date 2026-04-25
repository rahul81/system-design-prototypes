package org.prototypes.resourcemanager;

import org.prototypes.loadbalancer.Constants;

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
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private  static  final String uriPath = "/api/check";

    private static void checkServer(String target) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target + uriPath))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.format("Target Server : %s Response status: %d \n Response : %s%n", target, response.statusCode(), response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Health Check monitor started!");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(servers.length);
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
        for (String server : servers) {
            scheduler.scheduleAtFixedRate(() -> checkServer(server), 0, 5, TimeUnit.SECONDS);
        }
    }

}
