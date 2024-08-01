package model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskIds(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name=" + name +
                ", description.length='" + description.length() + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtaskIds=" + subtaskIds +
                '}';
    }

    @Override
    public String toStringForFile() {
        return id + "," +
                Tasks.EPIC.name() + "," +
                name + "," +
                status.name() + "," +
                description + "," + "";
    }
}