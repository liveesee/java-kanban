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
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        assertEquals(0, loadedManager.getAllTasks().size(), "Пустой файл должен загружаться без задач");
        assertEquals(0, loadedManager.getAllEpics().size(), "Пустой файл должен загружаться без эпиков");
        assertEquals(0, loadedManager.getAllSubtasks().size(), "Пустой файл должен загружаться без подзадач");
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Task task1 = new Task("t1", "td1");
        Task task2 = new Task("t2", "td2");
        manager.createTask(task1);
        manager.createTask(task2);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(2, loadedTasks.size(), "Должно быть загружено 2 задачи");
        assertEquals(task1.getTitle(), loadedTasks.get(0).getTitle(), "Заголовок первой задачи должен совпадать");
        assertEquals(task2.getTitle(), loadedTasks.get(1).getTitle(), "Заголовок второй задачи должен совпадать");
    }

    @Test
    public void shouldSaveAndLoadEpics() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Epic epic1 = new Epic("e1", "ed1");
        Epic epic2 = new Epic("e2", "ed2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Epic> loadedEpics = loadedManager.getAllEpics();
        assertEquals(2, loadedEpics.size(), "Должно быть загружено 2 эпика");
        assertEquals(epic1.getTitle(), loadedEpics.get(0).getTitle(), "Заголовок первого эпика должен совпадать");
        assertEquals(epic2.getTitle(), loadedEpics.get(1).getTitle(), "Заголовок второго эпика должен совпадать");
    }

    @Test
    public void shouldSaveAndLoadSubtasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Epic epic = new Epic("e1", "ed1");
        manager.createEpic(epic);
        
        Subtask subtask1 = new Subtask("s1", "sd1", epic.getId());
        Subtask subtask2 = new Subtask("s2", "sd2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();
        assertEquals(2, loadedSubtasks.size(), "Должно быть загружено 2 подзадачи");
        assertEquals(subtask1.getTitle(), loadedSubtasks.get(0).getTitle(), "Заголовок первой подзадачи должен совпадать");
        assertEquals(subtask2.getTitle(), loadedSubtasks.get(1).getTitle(), "Заголовок второй подзадачи должен совпадать");
    }

    @Test
    public void shouldRestoreNextId() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Task task1 = new Task("t1", "td1");
        Task task2 = new Task("t2", "td2");
        manager.createTask(task1);
        manager.createTask(task2);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        
        Task newTask = new Task("t3", "td3");
        loadedManager.createTask(newTask);
        
        assertEquals(3, newTask.getId(), "Новый ID должен быть 3");
    }

    @Test
    public void shouldWorkLikeInMemoryTaskManager() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Task task = new Task("t1", "td1");
        manager.createTask(task);
        
        Epic epic = new Epic("e1", "ed1");
        manager.createEpic(epic);
        
        Subtask subtask = new Subtask("s1", "sd1", epic.getId());
        manager.createSubtask(subtask);
        
        assertEquals(task, manager.getTaskById(task.getId()), "Должен находить задачу по ID");
        assertEquals(epic, manager.getEpicById(epic.getId()), "Должен находить эпик по ID");
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()), "Должен находить подзадачу по ID");
    }

    @Test
    public void shouldUpdateAndDeleteCorrectly() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        
        Task task = new Task("t1", "td1");
        manager.createTask(task);
        
        task.setTitle("updated");
        manager.updateTask(task);
        
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals("updated", loadedTasks.get(0).getTitle(), "Заголовок должен быть изменён");
        
        loadedManager.deleteTaskById(task.getId());
        assertEquals(0, loadedManager.getAllTasks().size(), "Задача должна быть удалена");
    }
}