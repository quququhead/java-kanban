package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.FileReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLOutput;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADLINE = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(Subtask subtask) {
        super.addTask(subtask);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Subtask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void updateTask(Epic epic) {
        super.updateTask(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADLINE);
            for (Epic epic : epics.values()) {
                writer.write("\n" + epic.toStringForFile());
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write("\n" + subtask.toStringForFile());
            }
            for (Task task : tasks.values()) {
                writer.write("\n" + task.toStringForFile());
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public Task fromString(String value) {
        if (value != null && !value.isBlank()) {
            String[] sortedValues = value.split(",");
            Status status = null;
            for (Status st : Status.values()) {
                if (st.name().equals(sortedValues[3])) {
                    status = st;
                }
            }
            Task task;
            if (Tasks.EPIC.name().equals(sortedValues[1])) {
                task = new Epic(sortedValues[2], sortedValues[4]);
                task.setId(Integer.parseInt(sortedValues[0]));
                task.setStatus(status);
            } else if (Tasks.SUBTASK.name().equals(sortedValues[1])) {
                task = new Subtask(sortedValues[2], sortedValues[4], status, Integer.parseInt(sortedValues[5]));
                task.setId(Integer.parseInt(sortedValues[0]));
            } else if (Tasks.TASK.name().equals(sortedValues[1])) {
                task = new Task(sortedValues[2], sortedValues[4], status);
                task.setId(Integer.parseInt(sortedValues[0]));
            } else {
                return null;
            }
            return task;
        } else {
            return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String taskToString = bufferedReader.readLine();
                if (!HEADLINE.equals(taskToString) && !taskToString.isBlank()) {
                    Task task = fileBackedTaskManager.fromString(taskToString);
                    String substring = taskToString.substring(taskToString.indexOf(",") + 1,
                            taskToString.indexOf(",", taskToString.indexOf(",") + 1));
                    if (Tasks.EPIC.name().equals(substring)) {
                        fileBackedTaskManager.addTask((Epic) task);
                    } else if (Tasks.SUBTASK.name().equals(substring)) {
                        fileBackedTaskManager.addTask((Subtask) task);
                    } else {
                        fileBackedTaskManager.addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileBackedTaskManager;
    }

    public static void main(String[] args) throws IOException {

        Epic epic = new Epic("Отпраздновать ДР", "На выходных");
        Subtask subtask1 = new Subtask("Собрать друзей", "Написать всем", Status.NEW, 0);
        Subtask subtask2 = new Subtask("Закупиться", "Больше еды и напитков", Status.NEW, 0);
        Subtask subtask3 = new Subtask("Найти домик", "На природе с баней", Status.NEW, 0);
        Epic emptyEpic = new Epic("Пустое название", "Пустое описание");
        Task task = new Task("Задача", "Описание задачи", Status.NEW);

        File temp = File.createTempFile("temps", ".txt");

        System.out.println(temp.getAbsolutePath());

        FileBackedTaskManager fbTManager = new FileBackedTaskManager(temp);

        System.out.println(Files.readString(temp.toPath()));

        fbTManager.addTask(epic);
        fbTManager.addTask(subtask1);
        fbTManager.addTask(subtask2);
        fbTManager.addTask(subtask3);
        fbTManager.addTask(emptyEpic);
        fbTManager.addTask(task);

        System.out.println(Files.readString(temp.toPath()));
    }
}