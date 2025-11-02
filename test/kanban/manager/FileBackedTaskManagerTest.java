package kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        fileBackedTaskManager.save();
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        assertEquals(0, loadedManager.getAllTasks().size(), "Пустой файл должен загружаться без задач");
        assertEquals(0, loadedManager.getAllEpics().size(), "Пустой файл должен загружаться без эпиков");
        assertEquals(0, loadedManager.getAllSubtasks().size(), "Пустой файл должен загружаться без подзадач");
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Task firstTask = new Task("task 1 name", "task 1 description");
        Task secondTask = new Task("task 2 name", "task 2 description");
        manager.createTask(firstTask);
        manager.createTask(secondTask);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(2, loadedTasks.size(), "Должно быть загружено 2 задачи");
        assertEquals(firstTask.getTitle(), loadedTasks.get(0).getTitle(), "Заголовок первой задачи должен совпадать");
        assertEquals(secondTask.getTitle(), loadedTasks.get(1).getTitle(), "Заголовок второй задачи должен совпадать");
    }

    @Test
    public void shouldSaveAndLoadEpics() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Epic firstEpic = new Epic("epic1name", "epic 1 ");
        Epic secondEpic = new Epic("epic1name", "ed2");
        manager.createEpic(firstEpic);
        manager.createEpic(secondEpic);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Epic> loadedEpics = loadedManager.getAllEpics();
        assertEquals(2, loadedEpics.size(), "Должно быть загружено 2 эпика");
        assertEquals(firstEpic.getTitle(), loadedEpics.get(0).getTitle(), "Заголовок первого эпика должен совпадать");
        assertEquals(secondEpic.getTitle(), loadedEpics.get(1).getTitle(), "Заголовок второго эпика должен совпадать");
    }

    @Test
    public void shouldSaveAndLoadSubtasks() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        
        Epic testEpic = new Epic("epic 1 name", "epic 1 description");
        fileBackedTaskManager.createEpic(testEpic);
        
        Subtask subtask1 = new Subtask("subtask 1 name", "subtask 1 description", testEpic.getId());
        Subtask subtask2 = new Subtask("subtask 2 name", "subtask 2 description", testEpic.getId());
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();
        assertEquals(2, loadedSubtasks.size(), "Должно быть загружено 2 подзадачи");
        assertEquals(subtask1.getTitle(), loadedSubtasks.get(0).getTitle(), "Заголовок первой подзадачи должен совпадать");
        assertEquals(subtask2.getTitle(), loadedSubtasks.get(1).getTitle(), "Заголовок второй подзадачи должен совпадать");
    }

    @Test
    public void shouldRestoreNextId() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        
        Task firstTask = new Task("task 1 name", "task 1 description");
        Task secondTask = new Task("task 2 name", "task 2 description");
        fileBackedTaskManager.createTask(firstTask);
        fileBackedTaskManager.createTask(secondTask);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        Task newTask = new Task("task 3 name", "task 3 description");
        loadedManager.createTask(newTask);
        
        assertEquals(3, newTask.getId(), "Новый ID должен быть 3");
    }

    @Test
    public void shouldWorkLikeInMemoryTaskManager() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        
        Task testTask = new Task("task 1 name", "task 1 description");
        fileBackedTaskManager.createTask(testTask);
        
        Epic taskEpic = new Epic("epic 1 name", "epic 1 description");
        fileBackedTaskManager.createEpic(taskEpic);
        
        Subtask subtask = new Subtask("subtask 1 name", "subtask 1 description", taskEpic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        
        assertEquals(testTask, fileBackedTaskManager.getTaskById(testTask.getId()), "Должен находить задачу по ID");
        assertEquals(taskEpic, fileBackedTaskManager.getEpicById(taskEpic.getId()), "Должен находить эпик по ID");
        assertEquals(subtask, fileBackedTaskManager.getSubtaskById(subtask.getId()), "Должен находить подзадачу по ID");
    }

    @Test
    public void shouldUpdateAndDeleteCorrectly() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        
        Task testTask = new Task("task 1 name", "task 1 description");
        fileBackedTaskManager.createTask(testTask);
        
        testTask.setTitle("updated task name");
        fileBackedTaskManager.updateTask(testTask);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals("updated task name", loadedTasks.get(0).getTitle(), "Заголовок должен быть изменён");
        
        loadedManager.deleteTaskById(testTask.getId());
        assertEquals(0, loadedManager.getAllTasks().size(), "Задача должна быть удалена");
    }
}