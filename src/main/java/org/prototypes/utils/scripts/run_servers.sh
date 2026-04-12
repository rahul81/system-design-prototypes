#!/bin/bash


BASE_DIR="$1"
# Cleanup function to kill all background processes
cleanup() {
    echo "Stopping all Java processes..."
    # Kill all processes in the same process group as this script
    # shellcheck disable=SC2046
    kill $(jobs -p) 2>/dev/null
    exit
}


# Trap SIGINT (Ctrl+C) and SIGTERM (Termination signal)
trap cleanup SIGINT SIGTERM

# Starting backend Java servers

java -cp "${BASE_DIR}/target/system-design-prototypes-1.0-SNAPSHOT.jar" org.prototypes.utils.servers.SimpleHttpServer server-1 8081 &
java -cp "${BASE_DIR}/system-design-prototypes-1.0-SNAPSHOT.jar" org.prototypes.utils.servers.SimpleHttpServer server-2 8082 &
java -cp "${BASE_DIR}/target/system-design-prototypes-1.0-SNAPSHOT.jar" org.prototypes.utils.servers.SimpleHttpServer server-3 8083 &
java -cp "${BASE_DIR}/target/system-design-prototypes-1.0-SNAPSHOT.jar" org.prototypes.utils.servers.SimpleHttpServer server-4 8084 &


echo "Processes are running. Press Ctrl+C to stop all."
wait
