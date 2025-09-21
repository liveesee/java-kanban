package model;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    public void setUp(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
        task = new Task("t1", "t1d");
    }

    @Test
    public void createNewTask(){
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не должна быть пустой");
        assertEquals(task, savedTask, "Задачи не равны");

        List<Task> taskList = taskManager.getAllTasks();
        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(taskList.get(0), task, "Задачи не равны");
        assertEquals(1, taskList.size(), "Неверное количество задач");
    }

    @Test
    public void shouldUpdateData(){
        task.setId(2);
        task.setTitle("updated");
        task.setDescription("updated description");
        task.setStatus(Status.DONE);
        assertEquals(2, task.getId(), "Неверный ID");
        assertEquals("updated", task.getTitle(), "Неверное имя");
        assertEquals("updated description", task.getDescription(), "Неверное описание");
        assertEquals(Status.DONE, task.getStatus(), "Неверный статус");
    }

    @Test
    public void tasksShouldBeEqualsIfHaveSameID(){
        Task task2 = new Task("t2", "t2d");
        task.setId(1);
        task2.setId(1);
        assertEquals(task.getId(), task2.getId(), "У задач неравен ID");
        assertEquals(task, task2, "Задачи не равны");
    }
}
