package service;

import model.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import org.junit.jupiter.api.Assertions;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void beforeEach() {
        setTaskManager();
    }

    @Override
    public void setTaskManager() {
        taskManager = new FileBackedTaskManager(new File("D:\\JetBrains\\Under\\Projects\\java-kanban",
                "tasks.txt"));
    }

    @Test
    public void testExceptionMethodLoadFromFile() {
        Assertions.assertThrows(ManagerSaveException.class, () ->
            FileBackedTaskManager.loadFromFile(new File("files/thisFileDoesNotExist.txt")),
                "Ошибка сохранения в несуществующий файл!");
    }

    @Test
    public void testExceptionMethodSave() {
        Epic epic0 = new Epic("Эпик0", "ОписаниеЭпик0");
        Assertions.assertThrows(ManagerSaveException.class, () -> new FileBackedTaskManager(
                new File("files/thisFileDoesNotExist.txt")).addTask(epic0),
                "Ошибка сохранения задачи в несуществующий файл!");
    }
}