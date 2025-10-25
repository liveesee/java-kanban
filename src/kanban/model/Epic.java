package kanban.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
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
}