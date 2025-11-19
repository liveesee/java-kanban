package kanban.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private HistoryManager historyManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        historyManager = Managers.getDefaultHistory();
        return new InMemoryTaskManager(historyManager);
    }

    @Test
    void shouldAlwaysReturnInitializedManagers() {
        HistoryManager history = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        assertNotNull(manager, "TaskManager должен быть проинициализирован");
        assertNotNull(history, "HistoryManager должен быть проинициализирован");
    }
}
