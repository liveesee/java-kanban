package kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
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
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task2.setId(5);
        taskManager.updateTask(task2);
        assertNotEquals(task1, task2, "Задачи не должны быть равны");
    }

    @Test
    public void taskShouldBeUnchangedAfterAddedToManager(){
        Task task = new Task("t1", "t1d");
        taskManager.createTask(task);
        Task sameTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), sameTask.getId());
        assertEquals(task.getTitle(), sameTask.getTitle());
        assertEquals(task.getDescription(), sameTask.getDescription());
        assertEquals(task.getStatus(), sameTask.getStatus());
    }

    @Test
    public void shouldFindAllTasks(){
        Task task1 = new Task("t1", "t1d");
        Task task2 = new Task("t2", "t2d");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> taskList = taskManager.getAllTasks();
        assertNotNull(taskList);
        assertEquals(taskList.get(0), task1);
        assertEquals(taskList.get(1), task2);
        assertEquals(2, taskList.size());
    }

    @Test
    public void shouldFindAllEpics(){
        Epic epic1 = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e2d");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        List<Epic> epicList = taskManager.getAllEpics();
        assertNotNull(epicList);
        assertEquals(epicList.get(0), epic1);
        assertEquals(epicList.get(1), epic2);
        assertEquals(2, epicList.size());
    }

    @Test
    public void shouldFindAllSubtasks(){
        Epic epic1 = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e2d");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId());
        Subtask subtask2 = new Subtask("s2", "s2d", epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertNotNull(subtaskList);
        assertEquals(subtaskList.get(0), subtask1);
        assertEquals(subtaskList.get(1), subtask2);
        assertEquals(2, subtaskList.size());
    }

    @Test
    public void shouldDeleteTasksByID(){
        Task task1 = new Task("t1", "t1d");
        taskManager.createTask(task1);
        taskManager.deleteTaskById(task1.getId());
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(0, taskList.size());
    }

    @Test
    public void shouldDeleteAllTasks(){
        Task task1 = new Task("t1", "t1d");
        Task task2 = new Task("t2", "t2d");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(0, taskList.size());
    }


    @Test
    public void shouldDeleteEpicsByID(){
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        taskManager.deleteEpicById(epic1.getId());
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(0, epicList.size());
    }

    @Test
    public void shouldDeleteAllEpics(){
        Epic epic1 = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e2d");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.deleteAllEpics();
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(0, epicList.size());
    }

    @Test
    public void shouldDeleteSubtasksByID(){
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtaskById(subtask1.getId());
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertEquals(0, subtaskList.size());
    }

    @Test
    public void shouldDeleteAllSubtasks(){
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId());
        Subtask subtask2 = new Subtask("s2", "s2d", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteAllSubtasks();
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertEquals(0, subtaskList.size());
    }

    @Test
    public void changesFromOutsideShouldNotAffectSavedTask() {
        Task task = new Task("t1", "d1");
        taskManager.createTask(task);
        Task savedBeforeChange = taskManager.getTaskById(task.getId());
        task.setTitle("changed");
        task.setDescription("changed outside");
        Task savedAfterChange = taskManager.getTaskById(task.getId());

        assertEquals(savedBeforeChange.getTitle(), savedAfterChange.getTitle(),
                "Изменения во внешнем объекте не должны влиять на менеджер");
        assertEquals(savedBeforeChange.getDescription(), savedAfterChange.getDescription(),
                "Данные внутри менеджера должны быть изолированы от внешних изменений");
    }


}
