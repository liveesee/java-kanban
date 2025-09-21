package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        Task copy = new Task(task.getTitle(), task.getDescription());
        copy.setId(task.getId());
        copy.setStatus(task.getStatus());
        boolean isListFull = history.size() == 10;
        if (isListFull) {
            history.remove(0);
        }
        history.add(copy);
    }
}
