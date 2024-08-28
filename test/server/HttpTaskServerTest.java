package server;

import com.google.gson.Gson;
import service.Managers;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {

    HttpTaskServer taskServer = new HttpTaskServer();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        HttpTaskServer.taskManager.clearTasks();
        HttpTaskServer.taskManager.clearSubtasks();
        HttpTaskServer.taskManager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        String taskJson = HttpTaskServer.gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }
}