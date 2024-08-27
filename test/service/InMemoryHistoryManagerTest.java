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
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        inMemoryHistoryManager.add(task0);
        Assertions.assertFalse(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveTask() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.remove(task0.getId());
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerBeEmpty() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ИсходноеОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task1.setId(1);
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.remove(task0.getId());
        inMemoryHistoryManager.remove(task1.getId());
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromHead() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ИсходноеОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task1.setId(1);
        Task task2 = new Task("Задача2", "ИсходноеОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task2.setId(2);
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task0.getId());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromMiddle() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ИсходноеОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task1.setId(1);
        Task task2 = new Task("Задача2", "ИсходноеОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task2.setId(2);
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task1.getId());
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(0, inMemoryHistoryManager.getHistory().getFirst().getId());
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().getLast().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerRemoveFromLast() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ИсходноеОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task1.setId(1);
        Task task2 = new Task("Задача2", "ИсходноеОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task2.setId(2);
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.remove(task2.getId());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getLast().getId());
    }

    @Test
    public void shouldInMemoryHistoryManagerReplaceFromHeadToLast() {
        Task task0 = new Task("Задача0", "ИсходноеОписаниеЗадача0", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 20, 0));
        Task task1 = new Task("Задача1", "ИсходноеОписаниеЗадача1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 21, 0));
        task1.setId(1);
        Task task2 = new Task("Задача2", "ИсходноеОписаниеЗадача2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 9, 18, 22, 0));
        task2.setId(2);
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task0);
        Assertions.assertEquals(3, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
        Assertions.assertEquals(0, inMemoryHistoryManager.getHistory().getLast().getId());
    }
}