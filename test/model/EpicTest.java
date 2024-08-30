package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
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
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeDoneWithDoneSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.DONE,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeInProgressWithNewAndDoneSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.DONE,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void shouldStatusOfEpicBeInProgressWithInProgressSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.IN_PROGRESS,
                1, Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.IN_PROGRESS,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }
}