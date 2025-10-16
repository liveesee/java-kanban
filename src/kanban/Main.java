package kanban;

import kanban.manager.*;
import kanban.model.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 2 подзадачами");
        Epic epic2 = new Epic("Эпик 2", "Эпик с 1 подзадачей");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask firstEpic1Subtask = new Subtask("Подзадача 1.1", "Описание 1", epic1.getId());
        Subtask secondEpic1Subtask = new Subtask("Подзадача 1.2", "Описание 2", epic1.getId());
        Subtask firstEpic2Subtask = new Subtask("Подзадача 2.1", "Описание 1", epic2.getId());
        taskManager.createSubtask(firstEpic1Subtask);
        taskManager.createSubtask(secondEpic1Subtask);
        taskManager.createSubtask(firstEpic2Subtask);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(firstEpic2Subtask.getId());
        taskManager.getSubtaskById(secondEpic1Subtask.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        System.out.println("\nИстория просмотров:");
        for (Task t : taskManager.getHistory()) {
            System.out.printf("%s ID=%d - %s%n", t.getClass().getSimpleName(), t.getId(), t.getTitle());
        }

        System.out.println("\nСтатусы до обновления:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.printf("Task ID=%d: %s%n", t.getId(), t.getStatus());
        }
        for (Epic e : taskManager.getAllEpics()) {
            System.out.printf("Epic ID=%d: %s%n", e.getId(), e.getStatus());
        }
        for (Subtask s : taskManager.getAllSubtasks()) {
            System.out.printf("Subtask ID=%d: %s%n", s.getId(), s.getStatus());
        }

        Task taskToUpdate = taskManager.getAllTasks().get(0);
        taskToUpdate.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskToUpdate);
        Epic epicToUpdate = taskManager.getAllEpics().get(0);
        Subtask subtaskToUpdate = taskManager.getAllSubtasks().get(0);
        subtaskToUpdate.setStatus(Status.DONE);
        taskManager.updateSubtask(subtaskToUpdate);
        taskManager.updateEpic(epicToUpdate);

        System.out.println("\nСтатусы после обновления:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.printf("Task ID=%d: %s%n", t.getId(), t.getStatus());
        }
        for (Epic e : taskManager.getAllEpics()) {
            System.out.printf("Epic ID=%d: %s%n", e.getId(), e.getStatus());
        }
        for (Subtask s : taskManager.getAllSubtasks()) {
            System.out.printf("Subtask ID=%d: %s%n", s.getId(), s.getStatus());
        }

        taskManager.deleteSubtaskById(taskManager.getAllSubtasks().get(1).getId());
        taskManager.deleteTaskById(task2.getId());
        System.out.println("\nПосле удаления задачи 2 и подзадачи 1.2:");
        System.out.printf("Задач: %d, Эпиков: %d, Подзадач: %d%n",
                taskManager.getAllTasks().size(),
                taskManager.getAllEpics().size(),
                taskManager.getAllSubtasks().size());

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        System.out.println("\nПосле полной очистки:");
        System.out.printf("Задач: %d, Эпиков: %d, Подзадач: %d%n",
                taskManager.getAllTasks().size(),
                taskManager.getAllEpics().size(),
                taskManager.getAllSubtasks().size());
    }
}
