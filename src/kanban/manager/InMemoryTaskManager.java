package kanban.manager;

import java.util.stream.Collectors;

import kanban.exception.TimeConflictException;
import kanban.exception.NotFoundException;
import kanban.model.Epic;
import kanban.model.Status;
import kanban.model.Subtask;
import kanban.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected HistoryManager historyManager;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected int nextId;
    protected  TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getId));
    }

    @Override
    public int generateId() {
        return nextId++;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Task::getId)
                .forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Subtask::getId)
                .forEach(historyManager::remove);
        subtasks.clear();
        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Subtask::getId)
                .forEach(historyManager::remove);
        subtasks.clear();
        epics.values().stream()
                .peek(Epic::clearSubtasks)
                .map(Epic::getId)
                .forEach(epicId -> {
                    updateEpicStatus(epicId);
                    updateEpicTimeFields(epicId);
                });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с идентификатором " + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с идентификатором " + id + " не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с идентификатором " + id + " не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        if (task.getStartTime() != null && !isTaskTimeValid(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующей задачей");
        }
        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        if (subtask.getId() == subtask.getEpicId()) {
            return;
        }
        if (subtask.getStartTime() != null && !isTaskTimeValid(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени с существующей задачей");
        }
        int newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        epic.addSubtaskId(newId);
        updateEpicStatus(epic.getId());
        updateEpicTimeFields(epic.getId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null && !(oldTask instanceof Epic) && !(oldTask instanceof Subtask)) {
            prioritizedTasks.remove(oldTask);
        }
        if (task.getStartTime() != null && !isTaskTimeValid(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующей задачей");
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            prioritizedTasks.remove(oldSubtask);
        }
        if (subtask.getStartTime() != null && !isTaskTimeValid(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени с существующей задачей");
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicTimeFields(subtask.getEpicId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().stream()
                    .map(subtasks::remove)
                    .filter(subtask -> subtask != null)
                    .forEach(prioritizedTasks::remove);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTimeFields(epic.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = subtaskIds.stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean allDone = subtaskIds.stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTimeFields(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> epicSubtasks = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .collect(Collectors.toList());
        epic.updateTimeFields(epicSubtasks);
    }

    private boolean isTimeCross(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return false;
        }
        LocalDateTime t1EndTime = t1.getEndTime();
        LocalDateTime t2EndTime = t2.getEndTime();
        if (t1EndTime == null || t2EndTime == null) {
            return false;
        }
        return t1.getStartTime().isBefore(t2EndTime)
                && t2.getStartTime().isBefore(t1EndTime);
    }

    private boolean isTaskTimeValid(Task newTask) {
        return prioritizedTasks.stream()
                .filter(taskInSet -> taskInSet.getId() != newTask.getId())
                .noneMatch(taskInSet -> isTimeCross(newTask, taskInSet));
    }
}

