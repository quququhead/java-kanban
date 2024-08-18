package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name=" + name +
                ", description.length='" + description.length() + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtaskIds=" + subtaskIds +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public String toStringForFile() {
        String result = id + "," +
                Tasks.EPIC.name() + "," +
                name + "," +
                status.name() + "," +
                description + "," + ",";

        if (duration != null) {
            result += duration.toMinutes() + ",";
        } else {
            result += null + ",";
        }

        return result + startTime;
    }
}