package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void addTask(Task task);

    void addTask(Subtask subtask);

    void addTask(Epic epic);

    void updateTask(Task task);

    void updateTask(Subtask subtask);

    void updateTask(Epic epic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Subtask> getEpicSubtasks(int id);
}