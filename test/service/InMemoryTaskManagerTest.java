package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void beforeEach() {
        setTaskManager();
    }

    @Override
    public void setTaskManager() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }
}