import model.Epic;
import model.Status;
import model.Subtask;
import service.FileBackedTaskManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
        Subtask subtask1 = new Subtask("Собрать друзей", "Написать всем", Status.NEW, 3); //0
        Subtask subtask2 = new Subtask("Закупиться", "Больше еды и напитков", Status.NEW, 3); //0
        Subtask subtask3 = new Subtask("Найти домик", "На природе с баней", Status.NEW, 3); //0
        Epic emptyEpic = new Epic("Пустое название", "Пустое описание");

        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(subtask1);
        inMemoryTaskManager.addTask(subtask2);
        inMemoryTaskManager.addTask(subtask3);
        inMemoryTaskManager.addTask(emptyEpic);

        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println(inMemoryTaskManager.getEpic(3)); //0
        System.out.println(inMemoryTaskManager.getSubtask(4)); //1
        System.out.println(inMemoryTaskManager.getSubtask(5)); //2
        System.out.println(inMemoryTaskManager.getSubtask(6)); //3
        System.out.println(inMemoryTaskManager.getEpic(7)); //4
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getSubtask(5)); //2
        System.out.println(inMemoryTaskManager.getSubtask(6)); //3
        System.out.println(inMemoryTaskManager.getSubtask(4)); //1
        System.out.println(inMemoryTaskManager.getEpic(7)); //7
        System.out.println(inMemoryTaskManager.getEpic(3)); //0
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        System.out.println(Files.readString(file.toPath()));
        System.out.println("-".repeat(60));
        inMemoryTaskManager.removeEpic(7); //4
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        inMemoryTaskManager.removeEpic(3); //0
        System.out.println(inMemoryTaskManager.getHistory());
    }
}