package service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;

public class InMemoryTaskManager implements TaskManager {
    private int idManager;
    public final HistoryManager inMemoryHistoryManager;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;

    public InMemoryTaskManager() {
        idManager = 0;
        inMemoryHistoryManager = Managers.getDefaultHistory();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        for (Task task : tasks.values()) {
            list.add(task);
        }
        return list;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> list = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            list.add(subtask);
        }
        return list;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> list = new ArrayList<>();
        for (Epic epic : epics.values()) {
            list.add(epic);
        }
        return list;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            syncEpic(epic);
        }
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addTask(Task task) {
        task.setId(idManager++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(idManager++);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskIds(subtask.getId());
            syncEpic(epic);
        }
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(idManager++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if ((subtasks.containsKey(subtask.getId())) && (epic != null)
                && (epic.getSubtaskIds().contains(subtask.getId()))) {
            subtasks.put(subtask.getId(), subtask);
            syncEpic(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicIn = epics.get(epic.getId());
            epicIn.setName(epic.getName());
            epicIn.setDescription(epic.getDescription());
        }
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskIds(subtask.getId());
            subtasks.remove(id);
            syncEpic(epic);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        List<Subtask> epicSubtask = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epicSubtask.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
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