package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Test
    public void shouldNotBeNullTaskList() {
        assertNotNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    public void shouldAddTask() {
        Task task1 = new Task("Задача1", "ИсходноеОписание", Status.NEW);
        inMemoryHistoryManager.add(task1);
        List<Task> list = inMemoryHistoryManager.getHistory();
        Task firstTask = list.getFirst();
        assertEquals(0, firstTask.getId());
    }
}