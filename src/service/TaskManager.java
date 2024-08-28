package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    Optional<Task> getTask(int id);

    Optional<Subtask> getSubtask(int id);

    Optional<Epic> getEpic(int id);

    boolean addTask(Task task);

    boolean addTask(Subtask subtask);

    void addTask(Epic epic);

    boolean updateTask(Task task);

    boolean updateTask(Subtask subtask);

    boolean updateTask(Epic epic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Subtask> getEpicSubtasks(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}