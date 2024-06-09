package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(Task task, int epicId) {
        this(task.name, task.description, task.status, epicId);
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name=" + name +
                ", description.length='" + description.length() + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}