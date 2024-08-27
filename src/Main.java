import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        File file = new File("D:\\JetBrains\\Under\\Projects\\java-kanban", "tasks.txt");
        //TaskManager inMemoryTaskManager = new FileBackedTaskManager(file); //Для теста, пустой файл

        System.out.println(Files.readString(file.toPath()));

        TaskManager inMemoryTaskManager = FileBackedTaskManager.loadFromFile(file); //Для теста, файл с задачами

        System.out.println(Files.readString(file.toPath()));

        //TaskManager inMemoryTaskManager = new InMemoryTaskManager(); //Для теста, переключить другую реализацию

        Epic epic = new Epic("Отпраздновать ДР", "На выходных");
        Subtask subtask1 = new Subtask("Собрать друзей", "Написать всем", Status.NEW, 3,
                Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 18, 0)); //0 //2
        Subtask subtask2 = new Subtask("Закупиться", "Больше еды и напитков", Status.NEW, 3,
                Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 19, 0)); //0 //2
        Subtask subtask3 = new Subtask("Найти домик", "На природе с баней", Status.NEW, 3,
                Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 20, 0)); //0 //2
        Epic emptyEpic = new Epic("Пустое название", "Пустое описание");
        Task task1 = new Task("Пустое название1", "Пустое описание1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 21, 0));
        Task task2 = new Task("Пустое название2", "Пустое описание2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 9, 18, 22, 0));

        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(subtask1);
        inMemoryTaskManager.addTask(subtask2);
        inMemoryTaskManager.addTask(subtask3);
        inMemoryTaskManager.addTask(emptyEpic);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);

        for (Task ts : inMemoryTaskManager.getPrioritizedTasks()) {
            System.out.println(ts);
        }

//        System.out.println(inMemoryTaskManager.getHistory());
//        System.out.println(inMemoryTaskManager.getEpic(3)); //0
//        System.out.println(inMemoryTaskManager.getSubtask(4)); //1
//        System.out.println(inMemoryTaskManager.getSubtask(5)); //2
//        System.out.println(inMemoryTaskManager.getSubtask(6)); //3
//        System.out.println(inMemoryTaskManager.getEpic(7)); //4
//        System.out.println("-".repeat(60));
//        System.out.println(inMemoryTaskManager.getHistory());
//        System.out.println("-".repeat(60));
//        System.out.println(inMemoryTaskManager.getSubtask(5)); //2
//        System.out.println(inMemoryTaskManager.getSubtask(6)); //3
//        System.out.println(inMemoryTaskManager.getSubtask(4)); //1
//        System.out.println(inMemoryTaskManager.getEpic(7)); //7
//        System.out.println(inMemoryTaskManager.getEpic(3)); //0
//        System.out.println("-".repeat(60));
//        System.out.println(inMemoryTaskManager.getHistory());
//        System.out.println("-".repeat(60));
//        System.out.println(Files.readString(file.toPath()));
//        System.out.println("-".repeat(60));
//        inMemoryTaskManager.removeEpic(7); //4
//        System.out.println(inMemoryTaskManager.getHistory());
//        System.out.println("-".repeat(60));
//        inMemoryTaskManager.removeEpic(3); //0
//        System.out.println(inMemoryTaskManager.getHistory());
//        inMemoryTaskManager.removeEpic(3); //0
//
//        System.out.println(inMemoryTaskManager.getPrioritizedTasks());
    }
}