package server;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpServer;
import model.Status;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {

    public static final int PORT = 8080;
    static TaskManager taskManager = Managers.getDefault();
    static final Gson gson = Managers.getGson();

    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritizied", new PrioritiziedHandler());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer httpUserServer = new HttpTaskServer();
        httpUserServer.start();
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        taskManager.addTask(task);
        System.out.println(taskManager.getTask(0));
        System.out.println(taskManager.getTasks());
        //String taskJson = HttpTaskServer.gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());

        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();
        System.out.println(tasksFromManager.size());
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
