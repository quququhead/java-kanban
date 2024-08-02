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

    private void save() throws ManagerSaveException {
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
            throw new ManagerSaveException("Ошибка сохранения!");
        }
    }

    private Task fromString(String value) {
        if (value != null && !value.isBlank()) {
            String[] sortedValues = value.split(",");
            Task task;
            if (Tasks.EPIC.name().equals(sortedValues[1])) {
                task = new Epic(sortedValues[2], sortedValues[4]);
                task.setId(Integer.parseInt(sortedValues[0]));
                task.setStatus(Status.valueOf(sortedValues[3]));
            } else if (Tasks.SUBTASK.name().equals(sortedValues[1])) {
                task = new Subtask(sortedValues[2], sortedValues[4],
                        Status.valueOf(sortedValues[3]), Integer.parseInt(sortedValues[5]));
                task.setId(Integer.parseInt(sortedValues[0]));
            } else if (Tasks.TASK.name().equals(sortedValues[1])) {
                task = new Task(sortedValues[2], sortedValues[4], Status.valueOf(sortedValues[3]));
                task.setId(Integer.parseInt(sortedValues[0]));
            } else {
                return null;
            }
            return task;
        } else {
            return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int newId = -1;
            while (bufferedReader.ready()) {
                String taskToString = bufferedReader.readLine();
                if (!HEADLINE.equals(taskToString) && !taskToString.isBlank()) {
                    Task task = fileBackedTaskManager.fromString(taskToString);
                    if (task.getId() > newId) {
                        newId = task.getId();
                    }
                    String substring = taskToString.substring(taskToString.indexOf(",") + 1,
                            taskToString.indexOf(",", taskToString.indexOf(",") + 1));
                    if (Tasks.EPIC.name().equals(substring)) {
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                    } else if (Tasks.SUBTASK.name().equals(substring)) {
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                    }
                }
            }
            fileBackedTaskManager.idManager = ++newId;
            for (Subtask sub : fileBackedTaskManager.subtasks.values()) {
                if (fileBackedTaskManager.epics.containsKey(sub.getEpicId())) {
                    Epic epic = fileBackedTaskManager.epics.get(sub.getEpicId());
                    epic.addSubtaskIds(sub.getId());
                } else {
                    fileBackedTaskManager.subtasks.remove(sub.getId());
                    fileBackedTaskManager.save();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения!");
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