import model.Epic;
import model.Status;
import model.Subtask;
import service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Отпраздновать ДР", "На выходных");
        Subtask subtask1 = new Subtask("Собрать друзей", "Написать всем", Status.NEW, 0);
        Subtask subtask2 = new Subtask("Закупиться", "Больше еды и напитков", Status.NEW, 0);
        Subtask subtask3 = new Subtask("Найти домик", "На природе с баней", Status.NEW, 0);
        Epic emptyEpic = new Epic("Пустое название", "Пустое описание");

        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(subtask1);
        inMemoryTaskManager.addTask(subtask2);
        inMemoryTaskManager.addTask(subtask3);
        inMemoryTaskManager.addTask(emptyEpic);

        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println(inMemoryTaskManager.getEpic(0));
        System.out.println(inMemoryTaskManager.getSubtask(1));
        System.out.println(inMemoryTaskManager.getSubtask(2));
        System.out.println(inMemoryTaskManager.getSubtask(3));
        System.out.println(inMemoryTaskManager.getEpic(4));
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getSubtask(2));
        System.out.println(inMemoryTaskManager.getSubtask(3));
        System.out.println(inMemoryTaskManager.getSubtask(1));
        System.out.println(inMemoryTaskManager.getEpic(4));
        System.out.println(inMemoryTaskManager.getEpic(0));
        System.out.println("-".repeat(60));
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        inMemoryTaskManager.removeEpic(4);
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("-".repeat(60));
        inMemoryTaskManager.removeEpic(0);
        System.out.println(inMemoryTaskManager.getHistory());
    }
}