package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public Task() {
        this.status = Status.NEW;
    }

    public Task(String title, String description, String startTime, String duration) {
        this.title = title;
        this.description = description;
        if (startTime != null && !startTime.isEmpty()) {
            this.startTime = LocalDateTime.parse(startTime, formatter);
        } else {
            this.startTime = null;
        }
        if (duration != null && !duration.isEmpty()) {
            this.duration = Duration.ofMinutes(Integer.parseInt(duration));
        } else {
            this.duration = null;
        }
        this.status = Status.NEW;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }
}
