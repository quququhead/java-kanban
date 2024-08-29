package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;

class InMemoryHistoryManagerTest {

    public static HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @BeforeEach
    void beforeEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldNotBeNullTaskList() {
        Assertions.assertNotNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    public void shouldInMemoryHistoryManagerAddTask() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        inMemoryHistoryManager.add(task1);
        Assertions.assertFalse(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveTask() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.remove(task1.getId());
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerBeEmpty() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task2.setId(1);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task1.getId());
        inMemoryHistoryManager.remove(task2.getId());
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromHead() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task2.setId(1);
        Task task3 = new Task("Задача3", "ОписаниеЗадача3", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task3.setId(2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task1.getId());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromMiddle() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task2.setId(1);
        Task task3 = new Task("Задача3", "ОписаниеЗадача3", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task3.setId(2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task2.getId());
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(0, inMemoryHistoryManager.getHistory().getFirst().getId());
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().getLast().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromLast() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task2.setId(1);
        Task task3 = new Task("Задача3", "ОписаниеЗадача3", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task3.setId(2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task3.getId());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getLast().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerReplaceFromHeadToLast() {
        Task task1 = new Task("Задача1", "ОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task2 = new Task("Задача2", "ОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task2.setId(1);
        Task task3 = new Task("Задача3", "ОписаниеЗадача3", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task3.setId(2);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.add(task1);
        Assertions.assertEquals(3, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
        Assertions.assertEquals(0, inMemoryHistoryManager.getHistory().getLast().getId());
    }
}