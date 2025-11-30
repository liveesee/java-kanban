package kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic() {
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
    }

    public Epic(String title, String description) {
        super(title, description, null, "0");
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateTimeFields(List<Subtask> subtasks) {

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = LocalDateTime.MAX;
        LocalDateTime latestEnd = LocalDateTime.MIN;
        boolean hasValidSubtask = false;

        for (Subtask subtask : subtasks) {
            if (subtask == null) {
                continue;
            }

            hasValidSubtask = true;

            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }

            if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
            }

            if (subtask.getEndTime() != null && subtask.getEndTime().isAfter(latestEnd)) {
                latestEnd = subtask.getEndTime();
            }
        }

        setStartTime(hasValidSubtask && earliestStart != LocalDateTime.MAX ? earliestStart : null);
        this.endTime = hasValidSubtask && latestEnd != LocalDateTime.MIN ? latestEnd : null;
        setDuration(hasValidSubtask ? totalDuration : null);
    }
}