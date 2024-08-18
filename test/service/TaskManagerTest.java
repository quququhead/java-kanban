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
        Task task0 = new Task("Задача0", "ОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(task0);
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(task0, taskManager.getTasks().getFirst());
    }

    @Test
    public void getSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(subtask1, taskManager.getSubtasks().getFirst());
    }

    @Test
    public void getEpics() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        taskManager.addTask(epic0);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(epic0, taskManager.getEpics().getFirst());
    }

    @Test
    public void clearTasks() {
        Task task0 = new Task("Задача0", "ОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(task0);
        taskManager.clearTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void clearSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.clearSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void clearEpics() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.clearEpics();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void getTask() {
        Task task0 = new Task("Задача0", "ОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(task0);
        Assertions.assertEquals(task0, taskManager.getTask(task0.getId()).get());
    }

    @Test
    public void getSubtask() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        Assertions.assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()).get());
    }

    @Test
    public void getEpic() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        taskManager.addTask(epic0);
        Assertions.assertEquals(epic0, taskManager.getEpic(epic0.getId()).get());
    }

    @Test
    public void addTask() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(task2);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(epic0, taskManager.getEpic(epic0.getId()).get());
        Assertions.assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()).get());
        Assertions.assertEquals(task2, taskManager.getTask(task2.getId()).get());
    }

    @Test
    public void updateTask() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 15, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 18, 17, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(task2);
        Epic epic3 = new Epic("Эпик3", "ОписаниеЭпик3");
        epic3.addSubtaskIds(subtask1.getId());
        taskManager.updateTask(epic3);
        Subtask subtask4 = new Subtask("Подзадача4", "ОписаниеПодзадача4", Status.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 18, 20, 0));
        subtask4.setId(1);
        taskManager.updateTask(subtask4);
        Task task5 = new Task("Задача5", "ОписаниеЗадача5", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 22, 0));
        task5.setId(2);
        taskManager.updateTask(task5);
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(epic0, taskManager.getEpics().getFirst());
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertEquals(subtask4, taskManager.getSubtasks().getFirst());
        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(task5, taskManager.getTasks().getFirst());
    }

    @Test
    public void removeTask() {
        Task task0 = new Task("Задача0", "ОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(task0);
        taskManager.removeTask(task0.getId());
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void removeSubtask() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.removeSubtask(subtask1.getId());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void removeEpic() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        taskManager.addTask(epic0);
        taskManager.removeEpic(epic0.getId());
        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void getEpicSubtasks() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Subtask subtask1 = new Subtask("Подзадача1", "ОписаниеПодзадача1", Status.NEW, 0,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 18, 20, 0));
        Subtask subtask2 = new Subtask("Подзадача2", "ОписаниеПодзадача2", Status.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 18, 21, 0));
        taskManager.addTask(epic0);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        Assertions.assertEquals(2, taskManager.getEpicSubtasks(epic0.getId()).size());
        Assertions.assertEquals(subtask1, taskManager.getEpicSubtasks(epic0.getId()).getFirst());
        Assertions.assertEquals(subtask2, taskManager.getEpicSubtasks(epic0.getId()).getLast());
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