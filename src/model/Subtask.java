package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(Task task) {
        this(task.name, task.description, task.status);
    }

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
        epicId = 0;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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