package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagerTest {

    @Test
    public void shouldNotBeNullTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void shouldNotBeNullHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }
}