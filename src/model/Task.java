package model;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected int id;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description.length='" + description.length() + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}