package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public Epic(String title, String description) {
        super(title, description, null, "0");
        this.subtaskIds = new ArrayList<>();
        this.startTime = null;
        this.endTime = null;
        this.duration = null;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return  startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateTimeFields(List<Subtask> subtasks) {

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        boolean hasValidSubtask = false;

        for (Subtask subtask : subtasks) {
            if (subtask == null) {
                continue;
            }

            hasValidSubtask = true;

            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }

            if (subtask.getStartTime() != null && (earliestStart == null || subtask.getStartTime().isBefore(earliestStart))) {
                    earliestStart = subtask.getStartTime();
            }

            if (subtask.getEndTime() != null && (latestEnd == null || subtask.getEndTime().isAfter(latestEnd))) {
                latestEnd = subtask.getEndTime();
            }
        }

        this.startTime = earliestStart;
        this.endTime = latestEnd;
        this.duration = hasValidSubtask ? totalDuration : null;
    }
}