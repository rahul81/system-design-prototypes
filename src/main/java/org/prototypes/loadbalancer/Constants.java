package org.prototypes.loadbalancer;

public class Constants {

    public static final String[] BACKEND_SERVERS = {
            "http://localhost:8081",
            "http://localhost:8082",
            "http://localhost:8083",
            "http://localhost:8084",
    };

    public static final int LOAD_BALANCER_PORT = 9000;
}
