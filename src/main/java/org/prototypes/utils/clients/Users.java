package org.prototypes.utils.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Users {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Function to simulate mock user requests
    void simulateUserRequests(int numRequests, String method, String url, String reqBody)  {

        for (int i = 0; i < numRequests; i++) {

            int finalI = i+1;
            Thread thread = new Thread(() -> {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                        .build();

                HttpResponse<String> response;
                try {
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.format("Req Id : %d Response status: %d \n Response : %s" , finalI ,response.statusCode(), response.body());

            });
            thread.start();

        }
    }

    public static void main(String[] args) {

        Users users = new Users();
        int numRequests = 1000;
        String method = "GET";
        String url = "http://localhost:9000/api/ping";
        String reqBody = "";
        users.simulateUserRequests(numRequests, method, url, reqBody);

    }
}
