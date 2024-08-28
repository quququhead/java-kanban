package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (Pattern.matches("^/subtasks$", path)) {
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getSubtasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = HttpTaskServer.parsePathId(pathId);
                        if (id != -1) {
                            String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getSubtask(id));
                            sendText(httpExchange, response);
                        } else {
                            sendNotFound(httpExchange, "Нет подзадачи с id: " + pathId + "!");
                        }
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "POST":
                    if (Pattern.matches("^/subtasks$", path)) {
                        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask = HttpTaskServer.gson.fromJson(body, Subtask.class);
                        if (HttpTaskServer.taskManager.getSubtask(subtask.getId()).isPresent()) {
                            if (HttpTaskServer.taskManager.updateTask(subtask)) {
                                httpExchange.sendResponseHeaders(201, 0);
                                return;
                            }
                        } else {
                            if (HttpTaskServer.taskManager.addTask(subtask)) {
                                httpExchange.sendResponseHeaders(201, 0);
                                return;
                            }
                        }
                        sendHasInteractions(httpExchange, "Задача пересекается с существующими!");
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "DELETE":
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        int id = HttpTaskServer.parsePathId(path.replaceFirst("/subtasks/", ""));
                        HttpTaskServer.taskManager.removeSubtask(id);
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getSubtask(id));
                        sendText(httpExchange, response);
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