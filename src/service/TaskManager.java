package service;

import java.util.ArrayList;
import java.util.HashMap;

import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;

public class TaskManager {
    private int idManager;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        idManager = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void addTask(Task task) {
        task.setId(idManager++);
        tasks.put(task.getId(), task);
    }

    public void addTask(Subtask subtask) {
        subtask.setId(idManager++);
        subtasks.put(subtask.getId(), subtask);
    }

    public void addTask(Epic epic) {
        epic.setId(idManager++);
        epics.put(epic.getId(), epic);
        syncEpic(epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        syncEpic(epics.get(subtask.getEpicId()));
    }

    public void updateTask(Epic epic) {
        epics.put(epic.getId(), epic);
        syncEpic(epic);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.remove((Integer) id);
        epic.setSubtaskIds(subtaskIds);
        subtasks.remove(id);
        syncEpic(epic);
    }

    public void removeEpicWithSubtask(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void removeEpicOnly(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtask.setEpicId(0);
        }
        epics.remove(id);
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(Epic epic) {
        HashMap<Integer, Subtask> epicSubtask = new HashMap<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            epicSubtask.put(subtaskId, subtask);
        }
        return epicSubtask;
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(int id) {
        HashMap<Integer, Subtask> epicSubtask = new HashMap<>();
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            epicSubtask.put(subtaskId, subtask);
        }
        return epicSubtask;
    }

    private void syncEpic(Epic epic) {
        int doneSubtasks = 0;
        int newSubtasks = 0;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtask.setEpicId(epic.getId());
            if (Status.DONE.equals(subtask.getStatus())) {
                doneSubtasks += 1;
            } else if (Status.NEW.equals(subtask.getStatus())) {
                newSubtasks += 1;
            }
        }
        if (epic.getSubtaskIds().isEmpty() || newSubtasks == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneSubtasks == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}