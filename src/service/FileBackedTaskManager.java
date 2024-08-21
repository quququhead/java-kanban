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
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADLINE = "id,type,name,status,description,epic,duration,startTime";
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
        if (value != null && !HEADLINE.equals(value) && !value.isBlank()) {
            String[] sortedValues = value.split(",");
            Task task;
            LocalDateTime startTime = null;
            Duration duration = null;
            if (!sortedValues[6].equals("null")) {
                duration = Duration.ofMinutes(Long.parseLong(sortedValues[6]));
            }
            if (!sortedValues[7].equals("null")) {
                startTime = LocalDateTime.parse(sortedValues[7]);
            }
            if (Tasks.EPIC.name().equals(sortedValues[1])) {
                task = new Epic(sortedValues[2], sortedValues[4]);
                task.setId(Integer.parseInt(sortedValues[0]));
                task.setStatus(Status.valueOf(sortedValues[3]));
                task.setDuration(null);
                task.setStartTime(null);
            } else if (Tasks.SUBTASK.name().equals(sortedValues[1])) {
                task = new Subtask(sortedValues[2], sortedValues[4], Status.valueOf(sortedValues[3]),
                        Integer.parseInt(sortedValues[5]), duration, startTime);
                task.setId(Integer.parseInt(sortedValues[0]));
            } else if (Tasks.TASK.name().equals(sortedValues[1])) {
                task = new Task(sortedValues[2], sortedValues[4],
                        Status.valueOf(sortedValues[3]), duration, startTime);
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
                Task task = fileBackedTaskManager.fromString(taskToString);
                if (task != null) {
                    String substring = taskToString.substring(taskToString.indexOf(",") + 1,
                            taskToString.indexOf(",", taskToString.indexOf(",") + 1));
                    if (Tasks.EPIC.name().equals(substring)) {
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        if (task.getId() > newId) {
                            newId = task.getId();
                        }
                    } else if (Tasks.SUBTASK.name().equals(substring)) {
                        if (task.getEndTime() != null && task.getStartTime().getMinute() % 15 == 0
                                && task.getEndTime().getMinute() % 15 == 0) {
                            if (fileBackedTaskManager.isCross(task)) {
                                fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                                fileBackedTaskManager.setTask.add(task);
                                LocalDateTime start = task.getStartTime();
                                LocalDateTime end = task.getEndTime();
                                do {
                                    fileBackedTaskManager.mapTask.put(start, false);
                                    start = start.plus(Duration.ofMinutes(15));
                                } while (!start.equals(end));
                                if (task.getId() > newId) {
                                    newId = task.getId();
                                }
                            }
                        } else {
                            fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                            if (task.getId() > newId) {
                                newId = task.getId();
                            }
                        }
                    } else {
                        if (task.getEndTime() != null && task.getStartTime().getMinute() % 15 == 0
                                && task.getEndTime().getMinute() % 15 == 0) {
                            if (fileBackedTaskManager.isCross(task)) {
                                fileBackedTaskManager.tasks.put(task.getId(), task);
                                fileBackedTaskManager.setTask.add(task);
                                LocalDateTime start = task.getStartTime();
                                LocalDateTime end = task.getEndTime();
                                do {
                                    fileBackedTaskManager.mapTask.put(start, false);
                                    start = start.plus(Duration.ofMinutes(15));
                                } while (!start.equals(end));
                                if (task.getId() > newId) {
                                    newId = task.getId();
                                }
                            }
                        } else {
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                            if (task.getId() > newId) {
                                newId = task.getId();
                            }
                        }
                    }
                }
            }
            fileBackedTaskManager.idManager = ++newId;
            for (Subtask sub : fileBackedTaskManager.subtasks.values()) {
                if (fileBackedTaskManager.epics.containsKey(sub.getEpicId())) {
                    Epic epic = fileBackedTaskManager.epics.get(sub.getEpicId());
                    epic.addSubtaskIds(sub.getId());
                } else {
                    if (sub.getEndTime() != null && sub.getStartTime().getMinute() % 15 == 0
                            && sub.getEndTime().getMinute() % 15 == 0) {
                        fileBackedTaskManager.setTask.remove(sub);
                        LocalDateTime start = sub.getStartTime();
                        LocalDateTime end = sub.getEndTime();
                        do {
                            fileBackedTaskManager.mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    }
                    fileBackedTaskManager.subtasks.remove(sub.getId());
                }
            }
            fileBackedTaskManager.epics.values().forEach(fileBackedTaskManager::syncEpic);
            fileBackedTaskManager.save();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения!");
        }
        return fileBackedTaskManager;
    }

    public static void main(String[] args) throws IOException {

        Epic epic = new Epic("Отпраздновать ДР", "На выходных");
        Subtask subtask1 = new Subtask("Собрать друзей", "Написать всем",
                Status.NEW, 0, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 9, 18, 20, 15));
        Subtask subtask2 = new Subtask("Закупиться", "Больше еды и напитков",
                Status.NEW, 0, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 18, 21, 15));
        Subtask subtask3 = new Subtask("Найти домик", "На природе с баней",
                Status.NEW, 0, Duration.ofMinutes(45),
                LocalDateTime.of(2024, 9, 18, 22, 15));
        Epic emptyEpic = new Epic("Пустое название", "Пустое описание");
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 9, 18, 16, 15));

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