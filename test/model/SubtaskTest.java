package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void shouldSubtasksEqualsWithSameIds() {
        Subtask subtask1 = new Subtask("Подзадача1", "ИсходноеОписание", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача1", "ИзмененноеОписание", Status.NEW, 1);
        assertEquals(subtask1.getId(), subtask2.getId());
    }
}