package service;

import model.Status;
import model.Subtask;
import model.Task;
import model.Epic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class InMemoryTaskManagerTest {

    private static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldHistoryManagerSaveDifferentTypesOfTaskAndTheirId() {
        Task task1 = new Task("Задача1", "ИсходноеОписание", Status.NEW);
        Subtask subtask1 = new Subtask("Подзадача1", "ИсходноеОписание", Status.NEW, 1);
        Epic epic1 = new Epic("Эпик1", "ИсходноеОписание");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(epic1);
        inMemoryTaskManager.addTask(subtask1);
        Task taskT1 = inMemoryTaskManager.getTask(0);
        Subtask subtaskS1 = inMemoryTaskManager.getSubtask(2);
        Epic epicE1 = inMemoryTaskManager.getEpic(1);
        List<Task> list = inMemoryTaskManager.getHistory();
        Task firstTask = list.get(0);
        Task secondEpic = list.get(1);
        Task thirdSubtask = list.get(2);
        assertNotNull(firstTask);
        assertNotNull(secondEpic);
        assertNotNull(thirdSubtask);
        assertEquals(taskT1.getId(), firstTask.getId());
        assertEquals(subtaskS1.getId(), secondEpic.getId());
        assertEquals(epicE1.getId(), thirdSubtask.getId());
    }

    @Test
    public void shouldTasksBeDifferentWithManualId() {
        Task task1 = new Task("Задача1", "ИсходноеОписание1", Status.NEW);
        Task task2 = new Task("Задача2", "ИсходноеОписание1", Status.NEW);
        task2.setId(0);
        inMemoryTaskManager.addTask(task1);
        Task taskT1 = inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.addTask(task2);
        Task taskT2 = inMemoryTaskManager.getTask(1);
        assertEquals(0, taskT1.getId());
        assertEquals(1, taskT2.getId());
    }

    @Test
    public void shouldTasksBeConstantAfterAdd() {
        Task task1 = new Task("Задача1", "ИсходноеОписание1", Status.NEW);
        inMemoryTaskManager.addTask(task1);
        Task taskT1 = inMemoryTaskManager.getTask(0);
        assertEquals("Задача1", taskT1.getName());
        assertEquals("ИсходноеОписание1", taskT1.getDescription());
        assertEquals(Status.NEW, taskT1.getStatus());
        assertEquals(0, taskT1.getId());
    }

    @Test
    public void shouldHistoryManagerSaveFirstVersionOfTask() {
        Task task1 = new Task("Задача1", "ИсходноеОписание", Status.NEW);
        Task task2 = new Task("Задача1", "ИзмененноеОписание", Status.NEW);
        inMemoryTaskManager.addTask(task1);
        Task taskT1 = inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.updateTask(task2);
        Task taskT2 = inMemoryTaskManager.getTask(0);
        List<Task> list = inMemoryTaskManager.getHistory();
        Task firstTask = list.getFirst();
        Task secondTask = list.getFirst();
        assertEquals("ИзмененноеОписание", firstTask.getDescription());
        assertEquals("ИзмененноеОписание", secondTask.getDescription());
    }
}