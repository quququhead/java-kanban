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
    protected final HistoryManager inMemoryHistoryManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;

    public InMemoryTaskManager() {
        idManager = 0;
        inMemoryHistoryManager = Managers.getDefaultHistory();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearTasks() {
        for (Integer key : tasks.keySet()) {
            inMemoryHistoryManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer key : subtasks.keySet()) {
            inMemoryHistoryManager.remove(key);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            syncEpic(epic);
        }
    }

    @Override
    public void clearEpics() {
        for (Integer key : epics.keySet()) {
            inMemoryHistoryManager.remove(key);
        }
        epics.clear();
        for (Integer key : subtasks.keySet()) {
            inMemoryHistoryManager.remove(key);
        }
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
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskIds(subtask.getId());
            subtasks.remove(id);
            syncEpic(epic);
            inMemoryHistoryManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                inMemoryHistoryManager.remove(subtaskId);
            }
            epics.remove(id);
            inMemoryHistoryManager.remove(id);
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