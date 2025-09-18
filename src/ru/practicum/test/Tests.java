package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class Tests {

    private InMemoryTaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void tasksShouldBeEqualsIfHaveSameID(){
        Task task1 = new Task("a","aaa");
        Task task2 = new Task("b","bbb");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task2, task1, "Задачи не равны");
    }

    @Test
    public void subclassesShouldBeEqualsIfHaveSameID(){
        Epic epic = new Epic("e1", "e1d");
        epic.setId(1);
        Subtask subtask = new Subtask("s1", "s1d",1);
        subtask.setId(1);
        assertEquals(epic, subtask, "Подклассы не равны");
    }

    @Test
    public void shouldNotBeAbleToAddEpicInsideItself(){
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask invalid = new Subtask("s1", "s1d", epic.getId());
        invalid.setId(epic.getId());
        taskManager.createSubtask(invalid);
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "");
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
    void shouldAlwaysReturnInitializedManagers() {
        HistoryManager history = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        assertNotNull(manager, "TaskManager должен быть проинициализирован");
        assertNotNull(history, "HistoryManager должен быть проинициализирован");
    }

    @Test
    public void shouldBeAbleToCreateEveryTypeOfTasksAndFindThemByID(){
        Task task = new Task("t1", "t1d");
        taskManager.createTask(task);
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", epic.getId());
        taskManager.createSubtask(subtask);
        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи должны находиться по ID");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики должны находиться по ID");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи должны находиться по ID");
    }

    @Test
    public void shouldNotBeConflictBetweenAutoAndManualIDSet(){
        Task task1 = new Task("t1", "t1d");
        Task task2 = new Task("t2", "t2d");
        task2.setId(5);
        assertNotEquals(task1, task2, "Задачи не должны быть равны");
    }

    @Test
    public void taskShouldBeUnchangedAfterAddedToManager(){
        Task task = new Task("s1", "s1d");
        taskManager.createTask(task);
        Task sameTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), sameTask.getId());
        assertEquals(task.getTitle(), sameTask.getTitle());
        assertEquals(task.getDescription(), sameTask.getDescription());
        assertEquals(task.getStatus(), sameTask.getStatus());
    }

    @Test
    public void historyShouldSavePreviousVersionOfTask(){
        Task original = new Task("t1", "original");
        taskManager.createTask(original);
        taskManager.getTaskById(original.getId());
        original.setDescription("updated");
        taskManager.updateTask(original);
        taskManager.getTaskById(original.getId());
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals("original", historyManager.getHistory().get(0).getDescription(),
                "История должна хранить старую версию");
        assertEquals("updated", historyManager.getHistory().get(1).getDescription(),
                "История должна хранить старую версию");
    }
}