package kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryMangerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private List<Task> history;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    @Test
    public void historyShouldSaveLastVersionOfTask(){
        Task original = new Task("t1", "original");
        taskManager.createTask(original);
        taskManager.getTaskById(original.getId());
        original.setDescription("updated");
        taskManager.updateTask(original);
        taskManager.getTaskById(original.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(),
                "История должна содержать только последнюю версию задачи");
        assertEquals("updated", history.get(0).getDescription(),
                "В истории должна быть обновлённая версия задачи");
    }

    @Test
    public void shouldSaveTasksInCorrectOrder() {
        Task task1 = new Task("t1", "d1");
        Task task2 = new Task("t2", "d2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Размер списка должен быть равен 2");
        assertEquals("t1", history.get(0).getTitle(), "Первый элемент должен быть t1");
        assertEquals("t2", history.get(1).getTitle(), "Второй элемент должен быть t2");
    }

    @Test
    public void shouldRemoveTasksFromHistory(){
        Task task1 = new Task("t1", "d1");
        Task task2 = new Task("t2", "d2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно быть 2 записи в истории");
        assertEquals("t1", history.get(0).getTitle(), "Первая запись должна быть t1");
        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должно быть 1 записи в истории");
        assertEquals("t2", history.get(0).getTitle(), "Первая запись должна быть t2");
        taskManager.deleteTaskById(task2.getId());
        history = historyManager.getHistory();
        assertEquals(0, history.size(), "Должно быть 0 записей в истории");
    }

}
