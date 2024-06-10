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

    public ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Task task : tasks.values()) {
            list.add(task);
        }
        return list;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> list = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            list.add(subtask);
        }
        return list;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Epic epic : epics.values()) {
            list.add(epic);
        }
        return list;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            syncEpic(epic);
        }
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
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
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(idManager++);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskIds(subtask.getId());
            syncEpic(epic);
        }
    }

    public void addTask(Epic epic) {
        epic.setId(idManager++);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if ((subtasks.containsKey(subtask.getId())) && (epic != null)
                && (epic.getSubtaskIds().contains(subtask.getId()))) {
            subtasks.put(subtask.getId(), subtask);
            syncEpic(epics.get(subtask.getEpicId()));
        }
    }

    public void updateTask(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicIn = epics.get(epic.getId());
            epicIn.setName(epic.getName());
            epicIn.setDescription(epic.getDescription());
        }
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskIds(subtask.getId());
            subtasks.remove(id);
            syncEpic(epic);
        }
    }

    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int id) {
        ArrayList<Subtask> epicSubtask = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epicSubtask.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtask;
    }

    private void syncEpic(Epic epic) {
        int newSubtasks = 0;
        int doneSubtasks = 0;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
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