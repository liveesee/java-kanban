package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EpicTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Epic epic;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
        epic = new Epic("e1", "e1d");
    }

    @Test
    public void createNewEpic(){
        taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Задача не должна быть пустой");
        assertEquals(epic, savedEpic, "Задачи не равны");

        List<Epic> epicList = taskManager.getAllEpics();
        assertNotNull(epicList, "Задачи не возвращаются");
        assertEquals(epicList.get(0), epic, "Задачи не равны");
        assertEquals(1, epicList.size(), "Неверное количество задач");
    }

    @Test
    public void shouldNotBeAbleToAddEpicInsideItself(){
        taskManager.createEpic(epic);
        Subtask invalid = new Subtask("s1", "s1d", epic.getId());
        invalid.setId(epic.getId());
        taskManager.createSubtask(invalid);
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "");
    }

    @Test
    public void shouldCreateWithoutSubtasks(){
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    public void shouldAddSubtaskId() {
        epic.addSubtaskId(1);
        assertTrue(epic.getSubtaskIds().contains(1));
    }

    @Test
    public void shouldRemoveSubtaskId() {
        epic.addSubtaskId(1);
        epic.removeSubtaskId(1);
        assertFalse(epic.getSubtaskIds().contains(1));
    }

    @Test
    public void shouldChangeStatusWhenSubtaskChangedStatus(){
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void subclassesShouldBeEqualsIfHaveSameID(){
        epic.setId(1);
        Subtask subtask = new Subtask("s1", "s1d",1);
        subtask.setId(1);
        assertEquals(epic, subtask, "Подклассы не равны");
    }
}
