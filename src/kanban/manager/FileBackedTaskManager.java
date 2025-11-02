package kanban.manager;

import kanban.model.Epic;
import kanban.model.Status;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public String toString(Task task) {
        if (task == null) {
            return null;
        }
        TaskType taskType;
        String stringFromTask;
        if (task instanceof Subtask) {
            taskType = TaskType.SUBTASK;
            Subtask subtask = (Subtask) task;
            stringFromTask = String.format("%d,%s,%s,%s,%s,%d",
                    task.getId(), taskType, task.getTitle(), task.getDescription(),
                    task.getStatus(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            taskType = TaskType.EPIC;
            stringFromTask = String.format("%d,%s,%s,%s,%s",
                    task.getId(), taskType, task.getTitle(), task.getDescription(), task.getStatus());
        } else {
            taskType = TaskType.TASK;
            stringFromTask = String.format("%d,%s,%s,%s,%s",
                    task.getId(), taskType, task.getTitle(), task.getDescription(), task.getStatus());
        }
        return stringFromTask;
    }

    public Task fromString(String string) {
        if (string == null) {
            return null;
        }
        String[] stringSplitArray = string.split(",");
        TaskType taskType = TaskType.valueOf(stringSplitArray[1].trim());
        int id = Integer.parseInt(stringSplitArray[0].trim());
        String title = stringSplitArray[2].trim();
        String description = stringSplitArray[3].trim();
        Status status = Status.valueOf(stringSplitArray[4].trim());
        Task taskFromString;
        if (taskType == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(stringSplitArray[5].trim());
            taskFromString = new Subtask(title, description, epicId);
        } else if (taskType == TaskType.EPIC) {
            taskFromString = new Epic(title, description);
        } else {
            taskFromString = new Task(title, description);
        }
        taskFromString.setId(id);
        taskFromString.setStatus(status);
        return taskFromString;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return manager;
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.isEmpty()) {
                    continue;
                }
                Task task = manager.fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
            int maxId = 0;
            for (Integer id : manager.tasks.keySet()) {
                if (id > maxId) maxId = id;
            }
            for (Integer id : manager.epics.keySet()) {
                if (id > maxId) maxId = id;
            }
            for (Integer id : manager.subtasks.keySet()) {
                if (id > maxId) maxId = id;
            }
            manager.nextId = maxId + 1;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
        return manager;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }
}
