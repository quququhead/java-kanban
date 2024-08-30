package server;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class HttpTaskServerTest {

    HttpTaskServer taskServer = new HttpTaskServer();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        HttpTaskServer.taskManager = Managers.getDefault();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 19, 0));

        String taskJson1 = HttpTaskServer.gson.toJson(task1);
        String taskJson2 = HttpTaskServer.gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());

        List<Task> tasksList = HttpTaskServer.gson.fromJson(response3.body(), new TaskListTypeToken().getType());
        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();

        Assertions.assertNotNull(tasksList);
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(2, tasksList.size());
        Assertions.assertEquals(2, tasksFromManager.size());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));

        String taskJson = HttpTaskServer.gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());

        Task taskDeserialized = HttpTaskServer.gson.fromJson(response2.body(), Task.class);

        Assertions.assertNotNull(taskDeserialized);
        Assertions.assertEquals("Задача1", taskDeserialized.getName());
        Assertions.assertEquals("ОписаниеЗадача1", taskDeserialized.getDescription());
    }

    @Test
    public void testPostAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));

        String taskJson = HttpTaskServer.gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Task taskDeserialized = HttpTaskServer.gson.fromJson(response.body(), Task.class);

        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();

        Assertions.assertNotNull(taskDeserialized);
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Задача1", tasksFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеЗадача1", tasksFromManager.getFirst().getDescription());
        Assertions.assertEquals("Задача1", taskDeserialized.getName());
        Assertions.assertEquals("ОписаниеЗадача1", taskDeserialized.getDescription());
    }

    @Test
    public void testPostUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));

        task2.setId(1);

        String taskJson1 = HttpTaskServer.gson.toJson(task1);
        String taskJson2 = HttpTaskServer.gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response2.statusCode());

        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Задача2", tasksFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеЗадача2", tasksFromManager.getFirst().getDescription());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));

        String taskJson = HttpTaskServer.gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());

        Task taskDeserialized = HttpTaskServer.gson.fromJson(response2.body(), Task.class);

        List<Task> tasksFromManager = HttpTaskServer.taskManager.getTasks();

        Assertions.assertNotNull(taskDeserialized);
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(0, tasksFromManager.size());
        Assertions.assertEquals("Задача1", taskDeserialized.getName());
        Assertions.assertEquals("ОписаниеЗадача1", taskDeserialized.getDescription());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);
        String subtaskJson3 = HttpTaskServer.gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson3)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response4.statusCode());

        List<Subtask> subtasksList = HttpTaskServer.gson.fromJson(response4.body(), new SubtaskListTypeToken().getType());
        List<Subtask> subtasksFromManager = HttpTaskServer.taskManager.getSubtasks();

        Assertions.assertNotNull(subtasksList);
        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(2, subtasksList.size());
        Assertions.assertEquals(2, subtasksFromManager.size());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());

        Subtask subtaskDeserialized = HttpTaskServer.gson.fromJson(response3.body(), Subtask.class);

        Assertions.assertNotNull(subtaskDeserialized);
        Assertions.assertEquals("Подзадача1", subtaskDeserialized.getName());
        Assertions.assertEquals("ОписаниеПодзадача1", subtaskDeserialized.getDescription());
    }

    @Test
    public void testPostAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());

        Subtask subtaskDeserialized = HttpTaskServer.gson.fromJson(response2.body(), Subtask.class);

        List<Subtask> subtasksFromManager = HttpTaskServer.taskManager.getSubtasks();

        Assertions.assertNotNull(subtaskDeserialized);
        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Подзадача1", subtasksFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеПодзадача1", subtasksFromManager.getFirst().getDescription());
        Assertions.assertEquals("Подзадача1", subtaskDeserialized.getName());
        Assertions.assertEquals("ОписаниеПодзадача1", subtaskDeserialized.getDescription());
    }

    @Test
    public void testPostUpdateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));

        subtask2.setId(2);

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);
        String subtaskJson3 = HttpTaskServer.gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson3)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response3.statusCode());

        List<Subtask> subtasksFromManager = HttpTaskServer.taskManager.getSubtasks();

        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Подзадача2", subtasksFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеПодзадача2", subtasksFromManager.getFirst().getDescription());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());

        Subtask subtaskDeserialized = HttpTaskServer.gson.fromJson(response3.body(), Subtask.class);

        List<Subtask> subtasksFromManager = HttpTaskServer.taskManager.getSubtasks();

        Assertions.assertNotNull(subtaskDeserialized);
        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(0, subtasksFromManager.size());
        Assertions.assertEquals("Подзадача1", subtaskDeserialized.getName());
        Assertions.assertEquals("ОписаниеПодзадача1", subtaskDeserialized.getDescription());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпик2");

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String epicJson2 = HttpTaskServer.gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());

        List<Epic> epicsList = HttpTaskServer.gson.fromJson(response3.body(), new EpicListTypeToken().getType());
        List<Epic> epicsFromManager = HttpTaskServer.taskManager.getEpics();

        Assertions.assertNotNull(epicsList);
        Assertions.assertNotNull(epicsFromManager);
        Assertions.assertEquals(2, epicsList.size());
        Assertions.assertEquals(2, epicsFromManager.size());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");

        String epicJson = HttpTaskServer.gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/epics/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());

        Epic epicDeserialized = HttpTaskServer.gson.fromJson(response2.body(), Epic.class);

        Assertions.assertNotNull(epicDeserialized);
        Assertions.assertEquals("Эпик1", epicDeserialized.getName());
        Assertions.assertEquals("ОписаниеЭпик1", epicDeserialized.getDescription());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String subtaskJson2 = HttpTaskServer.gson.toJson(subtask1);
        String subtaskJson3 = HttpTaskServer.gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson3)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response4.statusCode());

        List<Subtask> subtasksList = HttpTaskServer.gson.fromJson(response4.body(), new SubtaskListTypeToken().getType());
        List<Subtask> subtasksFromManager = HttpTaskServer.taskManager.getSubtasks();

        Assertions.assertNotNull(subtasksList);
        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(2, subtasksList.size());
        Assertions.assertEquals(2, subtasksFromManager.size());
    }

    @Test
    public void testPostAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");

        String epicJson = HttpTaskServer.gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Epic epicDeserialized = HttpTaskServer.gson.fromJson(response.body(), Epic.class);

        List<Epic> epicsFromManager = HttpTaskServer.taskManager.getEpics();

        Assertions.assertNotNull(epicDeserialized);
        Assertions.assertNotNull(epicsFromManager);
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Эпик1", epicsFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеЭпик1", epicsFromManager.getFirst().getDescription());
        Assertions.assertEquals("Эпик1", epicDeserialized.getName());
        Assertions.assertEquals("ОписаниеЭпик1", epicDeserialized.getDescription());
    }

    @Test
    public void testPostUpdateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпик2");

        epic2.setId(1);

        String epicJson1 = HttpTaskServer.gson.toJson(epic1);
        String epicJson2 = HttpTaskServer.gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response2.statusCode());

        List<Epic> epicsFromManager = HttpTaskServer.taskManager.getEpics();

        Assertions.assertNotNull(epicsFromManager);
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Эпик2", epicsFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеЭпик2", epicsFromManager.getFirst().getDescription());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");

        String epicJson = HttpTaskServer.gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/epics/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());

        Epic epicDeserialized = HttpTaskServer.gson.fromJson(response2.body(), Epic.class);

        List<Epic> epicsFromManager = HttpTaskServer.taskManager.getEpics();

        Assertions.assertNotNull(epicDeserialized);
        Assertions.assertNotNull(epicsFromManager);
        Assertions.assertEquals(0, epicsFromManager.size());
        Assertions.assertEquals("Эпик1", epicDeserialized.getName());
        Assertions.assertEquals("ОписаниеЭпик1", epicDeserialized.getDescription());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 18, 19, 0));
        Task task3 = new Task("Задача3", "ОписаниеЗадача3",
                Status.NEW, Duration.ofMinutes(45),
                LocalDateTime.of(2024, 9, 18, 20, 0));

        String taskJson1 = HttpTaskServer.gson.toJson(task1);
        String taskJson2 = HttpTaskServer.gson.toJson(task2);
        String taskJson3 = HttpTaskServer.gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/3");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        URI url4 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());


        URI url5 = URI.create("http://localhost:8080/history");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response7.statusCode());

        List<Task> tasksList = HttpTaskServer.gson.fromJson(response7.body(), new TaskListTypeToken().getType());
        List<Task> tasksFromManager = HttpTaskServer.taskManager.getHistory();

        Assertions.assertNotNull(tasksList);
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(3, tasksList.size());
        Assertions.assertEquals(3, tasksFromManager.size());
        Assertions.assertEquals("Задача3", tasksList.getFirst().getName());
        Assertions.assertEquals("ОписаниеЗадача3", tasksList.getFirst().getDescription());
        Assertions.assertEquals("Задача3", tasksFromManager.getFirst().getName());
        Assertions.assertEquals("ОписаниеЗадача3", tasksFromManager.getFirst().getDescription());
        Assertions.assertEquals("Задача1", tasksList.getLast().getName());
        Assertions.assertEquals("ОписаниеЗадача1", tasksList.getLast().getDescription());
        Assertions.assertEquals("Задача1", tasksFromManager.getLast().getName());
        Assertions.assertEquals("ОписаниеЗадача1", tasksFromManager.getLast().getDescription());
        Assertions.assertEquals("ОписаниеЗадача1", tasksFromManager.getLast().getDescription());
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1",
                Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 18, 19, 0));
        Task task3 = new Task("Задача3", "ОписаниеЗадача3",
                Status.NEW, Duration.ofMinutes(45),
                LocalDateTime.of(2024, 9, 18, 20, 0));

        String taskJson1 = HttpTaskServer.gson.toJson(task1);
        String taskJson2 = HttpTaskServer.gson.toJson(task2);
        String taskJson3 = HttpTaskServer.gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/prioritized");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response4.statusCode());

        Set<Task> tasksList = HttpTaskServer.gson.fromJson(response4.body(), new TaskSetTypeToken().getType());
        Set<Task> tasksFromManager = HttpTaskServer.taskManager.getPrioritizedTasks();

        Assertions.assertNotNull(tasksList);
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(3, tasksList.size());
        Assertions.assertEquals(3, tasksFromManager.size());
    }
}