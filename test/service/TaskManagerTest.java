package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    abstract void setTaskManager();

    @Test
    public void getTasks() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(task1);
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(task1, taskManager.getTasks().getFirst());
    }

    @Test
    public void getSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(subtask1, taskManager.getSubtasks().getFirst());
    }

    @Test
    public void getEpics() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        taskManager.addTask(epic1);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(epic1, taskManager.getEpics().getFirst());
    }

    @Test
    public void clearTasks() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(task1);
        taskManager.clearTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void clearSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.clearSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void clearEpics() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.clearEpics();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void getTask() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(task1);
        Assertions.assertEquals(task1, taskManager.getTask(task1.getId()).get());
    }

    @Test
    public void getSubtask() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        Assertions.assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()).get());
    }

    @Test
    public void getEpic() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        taskManager.addTask(epic1);
        Assertions.assertEquals(epic1, taskManager.getEpic(epic1.getId()).get());
    }

    @Test
    public void addTask() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(task1);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(epic1, taskManager.getEpic(epic1.getId()).get());
        Assertions.assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()).get());
        Assertions.assertEquals(task1, taskManager.getTask(task1.getId()).get());
    }

    @Test
    public void updateTask() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 15, 0));
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 17, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(task1);
        Epic epic2 = new Epic("Эпик2", "ОписаниеЭпик2");
        epic2.addSubtaskIds(subtask1.getId());
        taskManager.updateTask(epic2);
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.NEW, 1,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 20, 0));
        subtask2.setId(1);
        taskManager.updateTask(subtask2);
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 22, 0));
        task2.setId(2);
        taskManager.updateTask(task2);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(epic1, taskManager.getEpics().getFirst());
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(subtask1, taskManager.getSubtasks().getFirst());
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(task1, taskManager.getTasks().getFirst());
    }

    @Test
    public void removeTask() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(task1);
        taskManager.removeTask(task1.getId());
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void removeSubtask() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.removeSubtask(subtask1.getId());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void removeEpic() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        taskManager.addTask(epic1);
        taskManager.removeEpic(epic1.getId());
        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void getEpicSubtasks() {
        Epic epic1 = new Epic("Эпик1", "ОписаниеЭпик1");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 1,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.NEW, 1,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        taskManager.addTask(epic1);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(2, taskManager.getEpicSubtasks(epic1.getId()).size());
        Assertions.assertEquals(subtask1, taskManager.getEpicSubtasks(epic1.getId()).getFirst());
        Assertions.assertEquals(subtask2, taskManager.getEpicSubtasks(epic1.getId()).getLast());
    }

    @Test
    public void getHistory() {
        Assertions.assertNotNull(taskManager.getHistory());
    }

    @Test
    public void getPrioritizedTasks() {
        Assertions.assertNotNull(taskManager.getPrioritizedTasks());
    }
}