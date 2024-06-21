package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldTasksEqualsWithSameIds() {
        Task task1 = new Task("Задача1", "ИсходноеОписание", Status.NEW);
        Task task2 = new Task("Задача1", "ИзмененноеОписание", Status.NEW);
        assertEquals(task1.getId(), task2.getId());
    }
}