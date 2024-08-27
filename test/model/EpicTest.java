package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    public static TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldStatusOfEpicBeNewWithNewSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW,
                0, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.NEW,
                0, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.NEW, epic0.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeDoneWithDoneSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                0, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                0, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.DONE, epic0.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeInProgressWithNewAndDoneSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW,
                0, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                0, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic0.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeInProgressWithInProgressSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.IN_PROGRESS,
                0, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.IN_PROGRESS,
                0, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic0.getStatus());
    }
}