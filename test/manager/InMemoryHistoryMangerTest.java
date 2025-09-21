package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryMangerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    @Test
    public void historyShouldSavePreviousVersionOfTask(){
        Task original = new Task("t1", "original");
        taskManager.createTask(original);
        taskManager.getTaskById(original.getId());
        original.setDescription("updated");
        taskManager.updateTask(original);
        taskManager.getTaskById(original.getId());
        assertEquals("original", historyManager.getHistory().get(0).getDescription(),
                "История должна хранить старую версию");
        assertEquals("updated", historyManager.getHistory().get(1).getDescription(),
                "История должна хранить старую версию");
    }
}
