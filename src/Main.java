import service.TaskManager;

/*
 * Сначала всегда добавляется subtask без epicId, и только потом epic с заранее заполненным (вручную) subtaskIds!
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
    }
}