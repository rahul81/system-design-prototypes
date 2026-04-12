package org.prototypes.utils.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static class RequestHandler implements HttpHandler {

        private final String server_name;
        public RequestHandler(String server_name){

            this.server_name = server_name;
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Pong, from backend server : " + this.server_name;
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static void main(String[] args) throws IOException {

        final int PORT ;
        final String server_name;

        if (args.length < 2) {
            throw new IllegalArgumentException("Expected 2 arguments server name : String and PORT : Integer");
        }


        try {
            server_name = args[0];
            PORT = Integer.parseInt(args[1]);

            // start a listener on port 9000
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/ping", new RequestHandler(server_name));

            server.setExecutor(null);
            server.start();
            System.out.format(" Backend server %s listening on port : %d", server_name, PORT);

        } catch (Exception e) {
            System.out.println("Exception while parsing args : " + e.getMessage());
        }




    }
}
