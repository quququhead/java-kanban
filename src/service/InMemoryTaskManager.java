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
    protected final LocalDateTime now;
    protected final LocalDateTime end;

    public InMemoryTaskManager() {
        idManager = 1;
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
        LocalDateTime start = LocalDateTime.now(); // от какой даты
        now = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 0, 0);
        end = now.plus(Period.ofYears(1)); // до какой даты
        start = now;
        while (start.isBefore(end.plus(Duration.ofMinutes(15)))) {
            mapTask.put(start, true);
            start = start.plus(Duration.ofMinutes(15)); // кратность
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
    public boolean addTask(Task task) {
        if (task != null) {
            if (task.getEndTime() != null && task.getStartTime().isAfter(now)
                    && task.getStartTime().isBefore(end) && task.getEndTime().isBefore(end)) {
                if (isCross(task)) {
                    task.setId(idManager++);
                    tasks.put(task.getId(), task);
                    setTask.add(task);
                    int minute;
                    if (task.getStartTime().getMinute() < 15) {
                        minute = 0;
                    } else if (task.getStartTime().getMinute() < 30) {
                        minute = 15;
                    } else if (task.getStartTime().getMinute() < 45) {
                        minute = 30;
                    } else {
                        minute = 45;
                    }
                    LocalDateTime start = LocalDateTime.of(task.getStartTime().getYear(),
                            task.getStartTime().getMonth(), task.getStartTime().getDayOfMonth(),
                            task.getStartTime().getHour(), minute);
                    if (task.getEndTime().getMinute() < 15) {
                        minute = 0;
                    } else if (task.getEndTime().getMinute() < 30) {
                        minute = 15;
                    } else if (task.getEndTime().getMinute() < 45) {
                        minute = 30;
                    } else {
                        minute = 45;
                    }
                    LocalDateTime end = LocalDateTime.of(task.getEndTime().getYear(),
                            task.getEndTime().getMonth(), task.getEndTime().getDayOfMonth(),
                            task.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                    do {
                        mapTask.put(start, false);
                        start = start.plus(Duration.ofMinutes(15));
                    } while (!start.equals(end));
                    return true;
                }
            } else {
                task.setId(idManager++);
                tasks.put(task.getId(), task);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addTask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) {
                if (subtask.getEndTime() != null && subtask.getStartTime().isAfter(now)
                        && subtask.getStartTime().isBefore(end) && subtask.getEndTime().isBefore(end)) {
                    if (isCross(subtask)) {
                        subtask.setId(idManager++);
                        subtasks.put(subtask.getId(), subtask);
                        setTask.add(subtask);
                        int minute;
                        if (subtask.getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        LocalDateTime start = LocalDateTime.of(subtask.getStartTime().getYear(),
                                subtask.getStartTime().getMonth(), subtask.getStartTime().getDayOfMonth(),
                                subtask.getStartTime().getHour(), minute);
                        if (subtask.getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        LocalDateTime end = LocalDateTime.of(subtask.getEndTime().getYear(),
                                subtask.getEndTime().getMonth(), subtask.getEndTime().getDayOfMonth(),
                                subtask.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                        Epic epic = epics.get(subtask.getEpicId());
                        epic.addSubtaskIds(subtask.getId());
                        syncEpic(epic);
                        return true;
                    }
                } else {
                    subtask.setId(idManager++);
                    subtasks.put(subtask.getId(), subtask);
                    Epic epic = epics.get(subtask.getEpicId());
                    epic.addSubtaskIds(subtask.getId());
                    syncEpic(epic);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(idManager++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public boolean updateTask(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                setTask.remove(tasks.get(task.getId()));
                int minute;
                if (tasks.get(task.getId()).getStartTime().getMinute() < 15) {
                    minute = 0;
                } else if (tasks.get(task.getId()).getStartTime().getMinute() < 30) {
                    minute = 15;
                } else if (tasks.get(task.getId()).getStartTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime start = LocalDateTime.of(tasks.get(task.getId()).getStartTime().getYear(),
                        tasks.get(task.getId()).getStartTime().getMonth(),
                        tasks.get(task.getId()).getStartTime().getDayOfMonth(),
                        tasks.get(task.getId()).getStartTime().getHour(), minute);
                if (tasks.get(task.getId()).getEndTime().getMinute() < 15) {
                    minute = 0;
                } else if (tasks.get(task.getId()).getEndTime().getMinute() < 30) {
                    minute = 15;
                } else if (tasks.get(task.getId()).getEndTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime end = LocalDateTime.of(tasks.get(task.getId()).getEndTime().getYear(),
                        tasks.get(task.getId()).getEndTime().getMonth(),
                        tasks.get(task.getId()).getEndTime().getDayOfMonth(),
                        tasks.get(task.getId()).getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                do {
                    mapTask.put(start, true);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
                if (task.getEndTime() != null && task.getStartTime().isAfter(now)
                        && task.getStartTime().isBefore(end) && task.getEndTime().isBefore(end)) {
                    if (isCross(task)) {
                        tasks.put(task.getId(), task);
                        setTask.add(task);
                        if (task.getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (task.getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (task.getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        start = LocalDateTime.of(task.getStartTime().getYear(),
                                task.getStartTime().getMonth(), task.getStartTime().getDayOfMonth(),
                                task.getStartTime().getHour(), minute);
                        if (task.getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (task.getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (task.getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        end = LocalDateTime.of(task.getEndTime().getYear(),
                                task.getEndTime().getMonth(), task.getEndTime().getDayOfMonth(),
                                task.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                        return true;
                    } else {
                        setTask.add(tasks.get(task.getId()));
                        if (tasks.get(task.getId()).getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (tasks.get(task.getId()).getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (tasks.get(task.getId()).getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        start = LocalDateTime.of(tasks.get(task.getId()).getStartTime().getYear(),
                                tasks.get(task.getId()).getStartTime().getMonth(),
                                tasks.get(task.getId()).getStartTime().getDayOfMonth(),
                                tasks.get(task.getId()).getStartTime().getHour(), minute);
                        if (tasks.get(task.getId()).getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (tasks.get(task.getId()).getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (tasks.get(task.getId()).getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        end = LocalDateTime.of(tasks.get(task.getId()).getEndTime().getYear(),
                                tasks.get(task.getId()).getEndTime().getMonth(),
                                tasks.get(task.getId()).getEndTime().getDayOfMonth(),
                                tasks.get(task.getId()).getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    }
                } else {
                    tasks.put(task.getId(), task);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateTask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if ((subtasks.containsKey(subtask.getId())) && (epic != null)
                    && (epic.getSubtaskIds().contains(subtask.getId()))) {
                setTask.remove(subtasks.get(subtask.getId()));
                int minute;
                if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 15) {
                    minute = 0;
                } else if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 30) {
                    minute = 15;
                } else if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime start = LocalDateTime.of(subtasks.get(subtask.getId()).getStartTime().getYear(),
                        subtasks.get(subtask.getId()).getStartTime().getMonth(),
                        subtasks.get(subtask.getId()).getStartTime().getDayOfMonth(),
                        subtasks.get(subtask.getId()).getStartTime().getHour(), minute);
                if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 15) {
                    minute = 0;
                } else if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 30) {
                    minute = 15;
                } else if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime end = LocalDateTime.of(subtasks.get(subtask.getId()).getEndTime().getYear(),
                        subtasks.get(subtask.getId()).getEndTime().getMonth(),
                        subtasks.get(subtask.getId()).getEndTime().getDayOfMonth(),
                        subtasks.get(subtask.getId()).getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                do {
                    mapTask.put(start, true);
                    start = start.plus(Duration.ofMinutes(15));
                } while (!start.equals(end));
                if (subtask.getEndTime() != null && subtask.getStartTime().isAfter(now)
                        && subtask.getStartTime().isBefore(end) && subtask.getEndTime().isBefore(end)) {
                    if (epic.getStartTime().equals(subtask.getStartTime())) {
                        setTask.remove(epic);
                        if (epic.getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (epic.getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (epic.getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        start = LocalDateTime.of(epic.getStartTime().getYear(),
                                epic.getStartTime().getMonth(), epic.getStartTime().getDayOfMonth(),
                                epic.getStartTime().getHour(), minute);
                        if (epic.getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (epic.getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (epic.getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        end = LocalDateTime.of(epic.getEndTime().getYear(),
                                epic.getEndTime().getMonth(), epic.getEndTime().getDayOfMonth(),
                                epic.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, true);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    }
                    if (isCross(subtask)) {
                        subtasks.put(subtask.getId(), subtask);
                        setTask.add(subtask);
                        if (subtask.getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        start = LocalDateTime.of(subtask.getStartTime().getYear(),
                                subtask.getStartTime().getMonth(), subtask.getStartTime().getDayOfMonth(),
                                subtask.getStartTime().getHour(), minute);
                        if (subtask.getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        end = LocalDateTime.of(subtask.getEndTime().getYear(),
                                subtask.getEndTime().getMonth(), subtask.getEndTime().getDayOfMonth(),
                                subtask.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                        syncEpic(epic);
                        return true;
                    } else {
                        setTask.add(subtasks.get(subtask.getId()));
                        if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtasks.get(subtask.getId()).getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        start = LocalDateTime.of(subtasks.get(subtask.getId()).getStartTime().getYear(),
                                subtasks.get(subtask.getId()).getStartTime().getMonth(),
                                subtasks.get(subtask.getId()).getStartTime().getDayOfMonth(),
                                subtasks.get(subtask.getId()).getStartTime().getHour(), minute);
                        if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtasks.get(subtask.getId()).getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        end = LocalDateTime.of(subtasks.get(subtask.getId()).getEndTime().getYear(),
                                subtasks.get(subtask.getId()).getEndTime().getMonth(),
                                subtasks.get(subtask.getId()).getEndTime().getDayOfMonth(),
                                subtasks.get(subtask.getId()).getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    }
                } else {
                    subtasks.put(subtask.getId(), subtask);
                    syncEpic(epic);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateTask(Epic epic) {
        if (epic != null) {
            if (epics.containsKey(epic.getId())) {
                Epic epicIn = epics.get(epic.getId());
                epicIn.setName(epic.getName());
                epicIn.setDescription(epic.getDescription());
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id).getEndTime() != null && tasks.get(id).getStartTime().isAfter(now)
                && tasks.get(id).getStartTime().isBefore(end) && tasks.get(id).getEndTime().isBefore(end)) {
            setTask.remove(tasks.get(id));
            int minute;
            if (tasks.get(id).getStartTime().getMinute() < 15) {
                minute = 0;
            } else if (tasks.get(id).getStartTime().getMinute() < 30) {
                minute = 15;
            } else if (tasks.get(id).getStartTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime start = LocalDateTime.of(tasks.get(id).getStartTime().getYear(),
                    tasks.get(id).getStartTime().getMonth(), tasks.get(id).getStartTime().getDayOfMonth(),
                    tasks.get(id).getStartTime().getHour(), minute);
            if (tasks.get(id).getEndTime().getMinute() < 15) {
                minute = 0;
            } else if (tasks.get(id).getEndTime().getMinute() < 30) {
                minute = 15;
            } else if (tasks.get(id).getEndTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime end = LocalDateTime.of(tasks.get(id).getEndTime().getYear(),
                    tasks.get(id).getEndTime().getMonth(), tasks.get(id).getEndTime().getDayOfMonth(),
                    tasks.get(id).getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
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
            if (subtask.getEndTime() != null && subtask.getStartTime().isAfter(now)
                    && subtask.getStartTime().isBefore(end) && subtask.getEndTime().isBefore(end)) {
                setTask.remove(subtask);
                int minute;
                if (subtask.getStartTime().getMinute() < 15) {
                    minute = 0;
                } else if (subtask.getStartTime().getMinute() < 30) {
                    minute = 15;
                } else if (subtask.getStartTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime start = LocalDateTime.of(subtask.getStartTime().getYear(),
                        subtask.getStartTime().getMonth(), subtask.getStartTime().getDayOfMonth(),
                        subtask.getStartTime().getHour(), minute);
                if (subtask.getEndTime().getMinute() < 15) {
                    minute = 0;
                } else if (subtask.getEndTime().getMinute() < 30) {
                    minute = 15;
                } else if (subtask.getEndTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime end = LocalDateTime.of(subtask.getEndTime().getYear(),
                        subtask.getEndTime().getMonth(), subtask.getEndTime().getDayOfMonth(),
                        subtask.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
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
                        int minute;
                        if (subtask.getStartTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getStartTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getStartTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        LocalDateTime start = LocalDateTime.of(subtask.getStartTime().getYear(),
                                subtask.getStartTime().getMonth(), subtask.getStartTime().getDayOfMonth(),
                                subtask.getStartTime().getHour(), minute);
                        if (subtask.getEndTime().getMinute() < 15) {
                            minute = 0;
                        } else if (subtask.getEndTime().getMinute() < 30) {
                            minute = 15;
                        } else if (subtask.getEndTime().getMinute() < 45) {
                            minute = 30;
                        } else {
                            minute = 45;
                        }
                        LocalDateTime end = LocalDateTime.of(subtask.getEndTime().getYear(),
                                subtask.getEndTime().getMonth(), subtask.getEndTime().getDayOfMonth(),
                                subtask.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
                        do {
                            mapTask.put(start, false);
                            start = start.plus(Duration.ofMinutes(15));
                        } while (!start.equals(end));
                    })
                    .map(Subtask::getId)
                    .peek(inMemoryHistoryManager::remove)
                    .forEach(subtasks::remove);
            if (epic.getEndTime() != null && epic.getStartTime().isAfter(now)
                    && epic.getStartTime().isBefore(end) && epic.getEndTime().isBefore(end)) {
                setTask.remove(epic);
                int minute;
                if (epic.getStartTime().getMinute() < 15) {
                    minute = 0;
                } else if (epic.getStartTime().getMinute() < 30) {
                    minute = 15;
                } else if (epic.getStartTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime start = LocalDateTime.of(epic.getStartTime().getYear(),
                        epic.getStartTime().getMonth(), epic.getStartTime().getDayOfMonth(),
                        epic.getStartTime().getHour(), minute);
                if (epic.getEndTime().getMinute() < 15) {
                    minute = 0;
                } else if (epic.getEndTime().getMinute() < 30) {
                    minute = 15;
                } else if (epic.getEndTime().getMinute() < 45) {
                    minute = 30;
                } else {
                    minute = 45;
                }
                LocalDateTime end = LocalDateTime.of(epic.getEndTime().getYear(),
                        epic.getEndTime().getMonth(), epic.getEndTime().getDayOfMonth(),
                        epic.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
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
        int minute;
        if (task.getStartTime().getMinute() < 15) {
            minute = 0;
        } else if (task.getStartTime().getMinute() < 30) {
            minute = 15;
        } else if (task.getStartTime().getMinute() < 45) {
            minute = 30;
        } else {
            minute = 45;
        }
        LocalDateTime startTime = LocalDateTime.of(task.getStartTime().getYear(),
                task.getStartTime().getMonth(), task.getStartTime().getDayOfMonth(),
                task.getStartTime().getHour(), minute);
        if (task.getEndTime().getMinute() < 15) {
            minute = 0;
        } else if (task.getEndTime().getMinute() < 30) {
            minute = 15;
        } else if (task.getEndTime().getMinute() < 45) {
            minute = 30;
        } else {
            minute = 45;
        }
        LocalDateTime endTime = LocalDateTime.of(task.getEndTime().getYear(),
                task.getEndTime().getMonth(), task.getEndTime().getDayOfMonth(),
                task.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
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
            int minute;
            if (epic.getStartTime().getMinute() < 15) {
                minute = 0;
            } else if (epic.getStartTime().getMinute() < 30) {
                minute = 15;
            } else if (epic.getStartTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime start = LocalDateTime.of(epic.getStartTime().getYear(),
                    epic.getStartTime().getMonth(), epic.getStartTime().getDayOfMonth(),
                    epic.getStartTime().getHour(), minute);
            if (epic.getEndTime().getMinute() < 15) {
                minute = 0;
            } else if (epic.getEndTime().getMinute() < 30) {
                minute = 15;
            } else if (epic.getEndTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime end = LocalDateTime.of(epic.getEndTime().getYear(),
                    epic.getEndTime().getMonth(), epic.getEndTime().getDayOfMonth(),
                    epic.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
            do {
                mapTask.put(start, true);
                start = start.plus(Duration.ofMinutes(15));
            } while (!start.equals(end));
        } else if (epic.getEndTime() == null && endTime != null) {
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            epic.setEndTime(endTime);
            setTask.add(epic);
            int minute;
            if (epic.getStartTime().getMinute() < 15) {
                minute = 0;
            } else if (epic.getStartTime().getMinute() < 30) {
                minute = 15;
            } else if (epic.getStartTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime start = LocalDateTime.of(epic.getStartTime().getYear(),
                    epic.getStartTime().getMonth(), epic.getStartTime().getDayOfMonth(),
                    epic.getStartTime().getHour(), minute);
            if (epic.getEndTime().getMinute() < 15) {
                minute = 0;
            } else if (epic.getEndTime().getMinute() < 30) {
                minute = 15;
            } else if (epic.getEndTime().getMinute() < 45) {
                minute = 30;
            } else {
                minute = 45;
            }
            LocalDateTime end = LocalDateTime.of(epic.getEndTime().getYear(),
                    epic.getEndTime().getMonth(), epic.getEndTime().getDayOfMonth(),
                    epic.getEndTime().getHour(), minute).plus(Duration.ofMinutes(15));
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