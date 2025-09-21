package model;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);

    }

    @Test
    public void createNewSubtask(){
        epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        subtask = new Subtask("s1", "s1d", epic.getId());
        taskManager.createSubtask(subtask);

        List<Subtask> subtaskList = taskManager.getSubtasksByEpicId(epic.getId());
        assertNotNull(subtaskList);
        assertEquals(subtaskList.get(0), subtask);
        assertEquals(1, subtaskList.size());

        Task savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask);
        assertEquals(subtask, savedSubtask);
    }

    @Test
    void subtaskShouldNotBeItsOwnEpic() {
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", 1);
        subtask.setId(1);
        taskManager.createSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldSaveItsEpicID(){
        epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        subtask = new Subtask("s1", "s1d", epic.getId());
        taskManager.createSubtask(subtask);
        assertEquals(epic.getId(), subtask.getEpicId());
    }
}
