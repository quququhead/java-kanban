package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (Pattern.matches("^/epics$", path)) {
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getEpics());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = HttpTaskServer.parsePathId(pathId);
                        if (id != -1) {
                            String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getEpic(id));
                            sendText(httpExchange, response);
                            return;
                        }
                        sendNotFound(httpExchange, "Нет эпика с id: " + pathId + "!");
                        return;
                    }
                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = HttpTaskServer.parsePathId(pathId);
                        if (id != -1) {
                            if (HttpTaskServer.taskManager.getEpic(id).isPresent()) {
                                String response = HttpTaskServer.gson
                                        .toJson(HttpTaskServer.taskManager.getSubtask(id));
                                sendText(httpExchange, response);
                                return;
                            }
                        }
                        sendNotFound(httpExchange, "Нет эпика с id: " + pathId + "!");
                        return;
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "POST":
                    if (Pattern.matches("^/epics$", path)) {
                        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = HttpTaskServer.gson.fromJson(body, Epic.class);
                        if (HttpTaskServer.taskManager.getEpic(epic.getId()).isPresent()) {
                            if (HttpTaskServer.taskManager.updateTask(epic)) {
                                httpExchange.sendResponseHeaders(201, 0);
                                return;
                            }
                        } else {
                            HttpTaskServer.taskManager.addTask(epic);
                            httpExchange.sendResponseHeaders(201, 0);
                            return;
                        }
                    }
                    httpExchange.sendResponseHeaders(500, 0);
                    break;
                case "DELETE":
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        int id = HttpTaskServer.parsePathId(path.replaceFirst("/epics/", ""));
                        HttpTaskServer.taskManager.removeEpic(id);
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getEpic(id));
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