package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritiziedHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (Pattern.matches("^/prioritized$", path)) {
                        String response = HttpTaskServer.gson.toJson(HttpTaskServer.taskManager.getPrioritizedTasks());
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