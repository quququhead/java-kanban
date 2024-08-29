package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getTasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = HttpTaskServer.parsePathId(pathId);
                        if (id != -1) {
                            Optional<Task> task = HttpTaskServer.taskManager.getTask(id);
                            if (task.isPresent()) {
                                String response = HttpTaskServer.gson.toJson(task.get());
                                sendText(httpExchange, response);
                                return;
                            }
                        }
                        sendNotFound(httpExchange, "Нет задачи с id: " + pathId + "!");
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "POST":
                    if (Pattern.matches("^/tasks$", path)) {
                        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = HttpTaskServer.gson.fromJson(body, Task.class);
                        if (HttpTaskServer.taskManager.getTask(task.getId()).isPresent()) {
                            if (HttpTaskServer.taskManager.updateTask(task)) {
                                httpExchange.sendResponseHeaders(201, 0);
                                return;
                            }
                        } else {
                            if (HttpTaskServer.taskManager.addTask(task)) {
                                sendText(httpExchange, body);
                                return;
                            }
                        }
                        sendHasInteractions(httpExchange, "Задача пересекается с существующими!");
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = HttpTaskServer.parsePathId(pathId);
                        if (id != -1) {
                            Optional<Task> task = HttpTaskServer.taskManager.getTask(id);
                            if (task.isPresent()) {
                                String response = HttpTaskServer.gson.toJson(task.get());
                                HttpTaskServer.taskManager.removeTask(id);
                                sendText(httpExchange, response);
                                return;
                            }
                        }
                        sendNotFound(httpExchange, "Нет задачи с id: " + pathId + "!");
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(500, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }
}