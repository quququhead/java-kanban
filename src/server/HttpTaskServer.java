package server;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    static final int PORT = 8080;
    static TaskManager taskManager = Managers.getDefault();
    static final Gson gson = Managers.getGson();

    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer httpUserServer = new HttpTaskServer();
        httpUserServer.start();
        httpUserServer.stop();
    }

    public void start() {
        System.out.println("Started UserServer " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped UserServer " + PORT);
    }

    static int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
