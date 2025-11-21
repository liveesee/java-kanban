package kanban.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId, String startTime, String duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}