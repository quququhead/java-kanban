package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
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
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public String toStringForFile() {
        String result = id + "," +
                Tasks.SUBTASK.name() + "," +
                name + "," +
                status.name() + "," +
                description + "," +
                epicId + ",";

        if (duration != null) {
            result += duration.toMinutes() + ",";
        } else {
            result += null + ",";
        }

        return result + startTime;
    }
}