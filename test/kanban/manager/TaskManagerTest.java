package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void shouldBeAbleToCreateEveryTypeOfTasksAndFindThemByID() {
        Task task = new Task("t1", "t1d", "10:00 01.01.24", "60");
        taskManager.createTask(task);
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", epic.getId(), "11:00 01.01.24", "30");
        taskManager.createSubtask(subtask);
        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи должны находиться по ID");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики должны находиться по ID");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи должны находиться по ID");
    }

    @Test
    public void shouldNotBeConflictBetweenAutoAndManualIDSet() {
        Task task1 = new Task("t1", "t1d", "10:00 01.01.24", "60");
        Task task2 = new Task("t2", "t2d", "11:00 01.01.24", "30");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task2.setId(5);
        taskManager.updateTask(task2);
        assertNotEquals(task1, task2, "Задачи не должны быть равны");
    }

    @Test
    public void taskShouldBeUnchangedAfterAddedToManager() {
        Task task = new Task("t1", "t1d", "10:00 01.01.24", "60");
        taskManager.createTask(task);
        Task sameTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), sameTask.getId());
        assertEquals(task.getTitle(), sameTask.getTitle());
        assertEquals(task.getDescription(), sameTask.getDescription());
        assertEquals(task.getStatus(), sameTask.getStatus());
    }

    @Test
    public void shouldFindAllTasks() {
        Task task1 = new Task("t1", "t1d", "10:00 01.01.24", "60");
        Task task2 = new Task("t2", "t2d", "11:00 01.01.24", "30");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> taskList = taskManager.getAllTasks();
        assertNotNull(taskList);
        assertEquals(taskList.get(0), task1);
        assertEquals(taskList.get(1), task2);
        assertEquals(2, taskList.size());
    }

    @Test
    public void shouldFindAllEpics() {
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
    public void shouldFindAllSubtasks() {
        Epic epic1 = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e2d");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId(), "10:00 01.01.24", "60");
        Subtask subtask2 = new Subtask("s2", "s2d", epic2.getId(), "11:00 01.01.24", "30");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertNotNull(subtaskList);
        assertEquals(subtaskList.get(0), subtask1);
        assertEquals(subtaskList.get(1), subtask2);
        assertEquals(2, subtaskList.size());
    }

    @Test
    public void shouldDeleteTasksByID() {
        Task task1 = new Task("t1", "t1d", "10:00 01.01.24", "60");
        taskManager.createTask(task1);
        taskManager.deleteTaskById(task1.getId());
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(0, taskList.size());
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task1 = new Task("t1", "t1d", "10:00 01.01.24", "60");
        Task task2 = new Task("t2", "t2d", "11:00 01.01.24", "30");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(0, taskList.size());
    }

    @Test
    public void shouldDeleteEpicsByID() {
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        taskManager.deleteEpicById(epic1.getId());
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(0, epicList.size());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e2d");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.deleteAllEpics();
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(0, epicList.size());
    }

    @Test
    public void shouldDeleteSubtasksByID() {
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId(), "10:00 01.01.24", "60");
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtaskById(subtask1.getId());
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertEquals(0, subtaskList.size());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic1 = new Epic("e1", "e1d");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("s1", "s1d", epic1.getId(), "10:00 01.01.24", "60");
        Subtask subtask2 = new Subtask("s2", "s2d", epic1.getId(), "11:00 01.01.24", "30");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteAllSubtasks();
        List<Subtask> subtaskList = taskManager.getAllSubtasks();
        assertEquals(0, subtaskList.size());
    }

    @Test
    public void changesFromOutsideShouldNotAffectSavedTask() {
        Task task = new Task("t1", "d1", "10:00 01.01.24", "60");
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

    @Test
    public void shouldReturnPrioritizedTasks() {
        Task task1 = new Task("t1", "d1", "12:00 01.01.24", "60");
        Task task2 = new Task("t2", "d2", "10:00 01.01.24", "30");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", epic.getId(), "11:00 01.01.24", "45");
        taskManager.createSubtask(subtask);
        
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertNotNull(prioritized, "Приоритизированный список не должен быть null");
        assertEquals(3, prioritized.size(), "Должно быть 3 задачи в приоритизированном списке");
        
        assertEquals(task2, prioritized.get(0), "Первая задача должна быть task2");
        assertEquals(subtask, prioritized.get(1), "Вторая задача должна быть subtask");
        assertEquals(task1, prioritized.get(2), "Третья задача должна быть task1");
    }

    @Test
    public void shouldNotIncludeTasksWithNullStartTime() {
        Task task1 = new Task("t1", "d1", "10:00 01.01.24", "60");
        taskManager.createTask(task1);
        
        Task task2 = new Task("t2", "d2", "11:00 01.01.24", "30");
        taskManager.createTask(task2);
        task2.setStartTime(null);
        taskManager.updateTask(task2);
        
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size(), "В списке должна быть только одна задача");
        assertTrue(prioritized.contains(task1), "Список должен содержать task1");
        assertFalse(prioritized.contains(task2), "Список не должен содержать task2 с null startTime");
    }

    @Test
    public void subtaskShouldHaveLinkedEpic() {
        Epic epic = new Epic("e1", "e1d");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("s1", "s1d", epic.getId(), "10:00 01.01.24", "60");
        taskManager.createSubtask(subtask);
        
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача должна быть сохранена");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Подзадача должна иметь связанный эпик");
        
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик должен быть сохранен");
        assertTrue(savedEpic.getSubtaskIds().contains(subtask.getId()), 
                "Эпик должен содержать ID подзадачи");
        
        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertTrue(epicSubtasks.contains(subtask), 
                "Список подзадач эпика должен содержать созданную подзадачу");
    }

    @Test
    public void shouldDetectTimeIntervalCrossing() {
        Task task1 = new Task("t1", "d1", "10:00 01.01.24", "60");
        taskManager.createTask(task1);
        
        Task task2 = new Task("t2", "d2", "10:30 01.01.24", "30");
        
        assertThrows(TimeConflictException.class, () -> {
            taskManager.createTask(task2);
        }, "Задача с пересекающимся временем должна вызывать исключение");
        
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size(), "В приоритизированном списке должна быть только одна задача");
        assertTrue(prioritized.contains(task1), "Список должен содержать task1");
        assertFalse(prioritized.contains(task2), "Список не должен содержать task2 с пересекающимся временем");
    }

}

