package kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryMangerTest {
    private HistoryManager historyManager;
    private List<Task> history;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void historyShouldSaveLastVersionOfTask() {
        Task original = new Task("t1", "original", "10:00, 01.01.24", "60");
        original.setId(1);
        historyManager.add(original);
        
        Task updated = new Task("t1", "updated", "10:00, 01.01.24", "60");
        updated.setId(1);
        historyManager.add(updated);
        
        history = historyManager.getHistory();
        assertEquals(1, history.size(),
                "История должна содержать только последнюю версию задачи");
        assertEquals("updated", history.get(0).getDescription(),
                "В истории должна быть обновлённая версия задачи");
    }

    @Test
    public void shouldSaveTasksInCorrectOrder() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        task1.setId(1);
        task2.setId(2);
        
        historyManager.add(task1);
        historyManager.add(task2);
        
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Размер списка должен быть равен 2");
        assertEquals("t1", history.get(0).getTitle(), "Первый элемент должен быть t1");
        assertEquals("t2", history.get(1).getTitle(), "Второй элемент должен быть t2");
    }

    @Test
    public void shouldRemoveTasksFromHistory() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        task1.setId(1);
        task2.setId(2);
        
        historyManager.add(task1);
        historyManager.add(task2);
        
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно быть 2 записи в истории");
        assertEquals("t1", history.get(0).getTitle(), "Первая запись должна быть t1");
        
        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должно быть 1 записи в истории");
        assertEquals("t2", history.get(0).getTitle(), "Первая запись должна быть t2");
    }

    @Test
    public void shouldReturnEmptyHistoryWhenNoTasks() {
        history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertTrue(history.isEmpty(), "История должна быть пустой, когда нет задач");
        assertEquals(0, history.size(), "Размер пустой истории должен быть 0");
    }

    @Test
    public void shouldMoveDuplicateTaskToEnd() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        Task task3 = new Task("t3", "d3", "12:00, 01.01.24", "45");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        
        historyManager.add(task1);
        
        history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи (без дубликатов)");
        assertEquals("t2", history.get(0).getTitle(), "Первый элемент должен быть t2");
        assertEquals("t3", history.get(1).getTitle(), "Второй элемент должен быть t3");
        assertEquals("t1", history.get(2).getTitle(), "Последний элемент должен быть t1 (перемещен)");
    }

    @Test
    public void shouldRemoveTaskFromBeginning() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        Task task3 = new Task("t3", "d3", "12:00, 01.01.24", "45");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        
        historyManager.remove(task1.getId());
        
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals("t2", history.get(0).getTitle(), "Первая задача должна быть t2");
        assertEquals("t3", history.get(1).getTitle(), "Вторая задача должна быть t3");
    }

    @Test
    public void shouldRemoveTaskFromMiddle() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        Task task3 = new Task("t3", "d3", "12:00, 01.01.24", "45");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        
        historyManager.remove(task2.getId());
        
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals("t1", history.get(0).getTitle(), "Первая задача должна быть t1");
        assertEquals("t3", history.get(1).getTitle(), "Вторая задача должна быть t3");
    }

    @Test
    public void shouldRemoveTaskFromEnd() {
        Task task1 = new Task("t1", "d1", "10:00, 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "11:00, 01.01.24", "30");
        Task task3 = new Task("t3", "d3", "12:00, 01.01.24", "45");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        
        historyManager.remove(task3.getId());
        
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals("t1", history.get(0).getTitle(), "Первая задача должна быть t1");
        assertEquals("t2", history.get(1).getTitle(), "Вторая задача должна быть t2");
    }

}
