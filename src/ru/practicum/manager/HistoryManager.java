package ru.practicum.manager;

import ru.practicum.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    ArrayList<Task> getHistory();
}
