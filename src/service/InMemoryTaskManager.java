package service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;

public class InMemoryTaskManager implements TaskManager {
    protected int idManager;
    protected final HistoryManager inMemoryHistoryManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected final Set<Task> setTask;
    protected final Map<LocalDateTime, Boolean> mapTask;

    public InMemoryTaskManager() {
        idManager = 0;
        inMemoryHistoryManager = Managers.getDefaultHistory();
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        setTask = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;
            } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            } else {
                return task1.getId() - task2.getId();
            }
        });
        mapTask = new TreeMap<>((startTime1, startTime2) -> {
            if (startTime1.isAfter(startTime2)) {
                return 1;
            } else if (startTime1.isBefore(startTime2)) {
                return -1;
            } else {
                return 0;
            }
        });
        LocalDateTime now = LocalDateTime.now(); // от какой даты
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        LocalDateTime end = now.plus(Period.ofYears(1)); // до какой даты
        while (now.isBefore(end)) {
            mapTask.put(now, true);
            now = now.plus(Duration.ofMinutes(15)); // кратность
        }
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
        tasks.keySet().stream()
                .peek(inMemoryHistoryManager::remove)
                .map(tasks::get)
                .filter(task -> task.getEndTime() != null)
                .peek(task -> {
                    LocalDateTime start = task.getStartTime();
                    LocalDateTime end = task.getEndTime();
                    do {
                        mapTask.put(start, true);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                })
                .forEach(setTask::remove);
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().stream()
                .peek(inMemoryHistoryManager::remove)
                .map(subtasks::get)
                .filter(subtask -> subtask.getEndTime() != null)
                .peek(subtask -> {
                    LocalDateTime start = subtask.getStartTime();
                    LocalDateTime end = subtask.getEndTime();
                    do {
                        mapTask.put(start, true);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                })
                .forEach(setTask::remove);
        subtasks.clear();
        epics.values().stream()
                .peek(Epic::clearSubtaskIds)
                .forEach(this::syncEpic);
    }

    @Override
    public void clearEpics() {
        epics.keySet().stream()
                .peek(inMemoryHistoryManager::remove)
                .map(epics::get)
                .filter(epic -> epic.getEndTime() != null)
                .peek(setTask::remove)
                .map(Epic::getSubtaskIds)
                .flatMap(Collection::stream)
                .map(subtasks::get)
                .filter(subtask -> subtask.getEndTime() != null)
                .peek(subtask -> {
                    LocalDateTime start = subtask.getStartTime();
                    LocalDateTime end = subtask.getEndTime();
                    do {
                        mapTask.put(start, true);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                })
                .forEach(setTask::remove);
        epics.clear();
        subtasks.keySet().forEach(inMemoryHistoryManager::remove);
        subtasks.clear();
    }

    @Override
    public Optional<Task> getTask(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        if (tasks.get(id) == null) {
            return Optional.empty();
        } else {
            return Optional.of(tasks.get(id));
        }
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));
        if (subtasks.get(id) == null) {
            return Optional.empty();
        } else {
            return Optional.of(subtasks.get(id));
        }
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        if (epics.get(id) == null) {
            return Optional.empty();
        } else {
            return Optional.of(epics.get(id));
        }
    }

    @Override
    public void addTask(Task task) {
        if (task.getEndTime() != null && task.getStartTime().getMinute() % 15 == 0
                && task.getEndTime().getMinute() % 15 == 0) {
            if (isCross(task)) {
                task.setId(idManager++);
                tasks.put(task.getId(), task);
                setTask.add(task);
                LocalDateTime start = task.getStartTime();
                LocalDateTime end = task.getEndTime();
                do {
                    mapTask.put(start, false);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
            }
        } else {
            task.setId(idManager++);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            if (subtask.getEndTime() != null && subtask.getStartTime().getMinute() % 15 == 0
                    && subtask.getEndTime().getMinute() % 15 == 0) {
                if (isCross(subtask)) {
                    subtask.setId(idManager++);
                    subtasks.put(subtask.getId(), subtask);
                    setTask.add(subtask);
                    LocalDateTime start = subtask.getStartTime();
                    LocalDateTime end = subtask.getEndTime();
                    do {
                        mapTask.put(start, false);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                    Epic epic = epics.get(subtask.getEpicId());
                    epic.addSubtaskIds(subtask.getId());
                    syncEpic(epic);
                }
            } else {
                subtask.setId(idManager++);
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtaskIds(subtask.getId());
                syncEpic(epic);
            }
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
            setTask.remove(tasks.get(task.getId()));
            LocalDateTime start = tasks.get(task.getId()).getStartTime();
            LocalDateTime end = tasks.get(task.getId()).getEndTime();
            do {
                mapTask.put(start, true);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
            if (task.getEndTime() != null && task.getStartTime().getMinute() % 15 == 0
                    && task.getEndTime().getMinute() % 15 == 0) {
                if (isCross(task)) {
                    tasks.put(task.getId(), task);
                    setTask.add(task);
                    start = task.getStartTime();
                    end = task.getEndTime();
                } else {
                    setTask.add(tasks.get(task.getId()));
                    start = tasks.get(task.getId()).getStartTime();
                    end = tasks.get(task.getId()).getEndTime();
                }
                do {
                    mapTask.put(start, false);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
            } else {
                tasks.put(task.getId(), task);
            }
        }
    }

    @Override
    public void updateTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if ((subtasks.containsKey(subtask.getId())) && (epic != null)
                && (epic.getSubtaskIds().contains(subtask.getId()))) {
            setTask.remove(subtasks.get(subtask.getId()));
            LocalDateTime start = subtasks.get(subtask.getId()).getStartTime();
            LocalDateTime end = subtasks.get(subtask.getId()).getEndTime();
            do {
                mapTask.put(start, true);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
            if (subtask.getEndTime() != null && subtask.getStartTime().getMinute() % 15 == 0
                    && subtask.getEndTime().getMinute() % 15 == 0) {
                if (epic.getStartTime().equals(subtask.getStartTime())) {
                    setTask.remove(epic);
                    start = epic.getStartTime();
                    end = epic.getEndTime();
                    do {
                        mapTask.put(start, true);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                }
                if (isCross(subtask)) {
                    subtasks.put(subtask.getId(), subtask);
                    setTask.add(subtask);
                    start = subtask.getStartTime();
                    end = subtask.getEndTime();
                    do {
                        mapTask.put(start, false);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                    syncEpic(epic);
                } else {
                    setTask.add(subtasks.get(subtask.getId()));
                    start = subtasks.get(subtask.getId()).getStartTime();
                    end = subtasks.get(subtask.getId()).getEndTime();
                    do {
                        mapTask.put(start, false);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                }
            } else {
                subtasks.put(subtask.getId(), subtask);
                syncEpic(epic);
            }
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
        if (tasks.get(id).getEndTime() != null && tasks.get(id).getStartTime().getMinute() % 15 == 0
                && tasks.get(id).getEndTime().getMinute() % 15 == 0) {
            setTask.remove(tasks.get(id));
            LocalDateTime start = tasks.get(id).getStartTime();
            LocalDateTime end = tasks.get(id).getEndTime();
            do {
                mapTask.put(start, true);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
        }
        tasks.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskIds(id);
            if (subtask.getEndTime() != null && subtask.getStartTime().getMinute() % 15 == 0
                    && subtask.getEndTime().getMinute() % 15 == 0) {
                setTask.remove(subtask);
                LocalDateTime start = subtask.getStartTime();
                LocalDateTime end = subtask.getEndTime();
                do {
                    mapTask.put(start, true);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
            }
            subtasks.remove(id);
            syncEpic(epic);
            inMemoryHistoryManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .peek(setTask::remove)
                    .peek(subtask -> {
                        LocalDateTime start = subtask.getStartTime();
                        LocalDateTime end = subtask.getEndTime();
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    })
                    .map(Subtask::getId)
                    .peek(inMemoryHistoryManager::remove)
                    .forEach(subtasks::remove);
            if (epic.getEndTime() != null && epic.getStartTime().getMinute() % 15 == 0
                    && epic.getEndTime().getMinute() % 15 == 0) {
                setTask.remove(epic);
                LocalDateTime start = epic.getStartTime();
                LocalDateTime end = epic.getEndTime();
                do {
                    mapTask.put(start, true);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
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
            epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .forEach(epicSubtask::add);
        }
        return epicSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return setTask;
    }

    protected boolean isCross(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        do {
            if (!mapTask.get(startTime)) {
                return false;
            }
            startTime = startTime.plus(Duration.ofMinutes(15));
        } while (!startTime.equals(endTime.plus(Duration.ofMinutes(15))));
        return true;
    }

    protected void syncEpic(Epic epic) {
        int newSubtasks = 0;
        int doneSubtasks = 0;
        LocalDateTime startTime = null;
        Duration duration = null;
        LocalDateTime endTime = null;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (Status.DONE.equals(subtask.getStatus())) {
                doneSubtasks += 1;
            } else if (Status.NEW.equals(subtask.getStatus())) {
                newSubtasks += 1;
            }
            if (subtask.getStartTime() != null) {
                if (startTime == null || startTime.isAfter(subtask.getStartTime())) {
                    startTime = subtask.getStartTime();
                }
            }
            if (subtask.getDuration() != null) {
                if (duration == null) {
                    duration = subtask.getDuration();
                } else {
                    duration = duration.plus(subtask.getDuration());
                }
            }
            if (subtask.getEndTime() != null) {
                if (endTime == null || endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }
        }
        if (epic.getSubtaskIds().isEmpty() || newSubtasks == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneSubtasks == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (epic.getEndTime() != null && endTime == null) {
            setTask.remove(epic);
            LocalDateTime start = epic.getStartTime();
            LocalDateTime end = epic.getEndTime();
            do {
                mapTask.put(start, true);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
        } else if (epic.getEndTime() == null && endTime != null) {
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            epic.setEndTime(endTime);
            setTask.add(epic);
            LocalDateTime start = epic.getStartTime();
            LocalDateTime end = epic.getEndTime();
            do {
                mapTask.put(start, false);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
        } else {
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            epic.setEndTime(endTime);
        }
    }
}