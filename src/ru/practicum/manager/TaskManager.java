package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    int generateId();

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    ArrayList<Task> getHistory();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    void updateEpicStatus(int epicId);
}
