package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void shouldEpicsEqualsWithSameIds() {
        Epic epic1 = new Epic("Эпик1", "ИсходноеОписание");
        Epic epic2 = new Epic("Эпик1", "ИзмененноеОписание");
        assertEquals(epic1.getId(), epic2.getId());
    }
}