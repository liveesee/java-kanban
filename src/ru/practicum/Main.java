package ru.practicum;

import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Эпик с двумя подзадачами", "Описание эпика 1");
        manager.createEpic(epic1);
        int epic1Id = manager.getAllEpics().get(0).getId();

        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epic1Id);
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epic1Id);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик с одной подзадачей", "Описание эпика 2");
        manager.createEpic(epic2);
        int epic2Id = manager.getAllEpics().get(1).getId();

        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", epic2Id);
        manager.createSubtask(subtask3);

        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        Task updatedTask1 = manager.getTaskById(1);
        updatedTask1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(updatedTask1);

        Task updatedTask2 = manager.getTaskById(2);
        updatedTask2.setStatus(Status.DONE);
        manager.updateTask(updatedTask2);

        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        if (allSubtasks.size() >= 3) {
            allSubtasks.get(0).setStatus(Status.DONE);
            manager.updateSubtask(allSubtasks.get(0));

            allSubtasks.get(1).setStatus(Status.DONE);
            manager.updateSubtask(allSubtasks.get(1));

            allSubtasks.get(2).setStatus(Status.IN_PROGRESS);
            manager.updateSubtask(allSubtasks.get(2));
        }

        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        Task finalTask1 = manager.getTaskById(1);
        Task finalTask2 = manager.getTaskById(2);
        System.out.println("Статус задачи 1: " + finalTask1.getStatus() + " (должен быть IN_PROGRESS)");
        System.out.println("Статус задачи 2: " + finalTask2.getStatus() + " (должен быть DONE)");

        Subtask finalSubtask1 = allSubtasks.get(0);
        Subtask finalSubtask2 = allSubtasks.get(1);
        Subtask finalSubtask3 = allSubtasks.get(2);
        System.out.println("Статус подзадачи 1.1: " + finalSubtask1.getStatus() + " (должен быть DONE)");
        System.out.println("Статус подзадачи 1.2: " + finalSubtask2.getStatus() + " (должен быть DONE)");
        System.out.println("Статус подзадачи 2.1: " + finalSubtask3.getStatus() + " (должен быть IN_PROGRESS)");

        Epic finalEpic1 = manager.getEpicById(3);
        Epic finalEpic2 = manager.getEpicById(6);
        System.out.println("Статус эпика 1: " + finalEpic1.getStatus() + " (должен быть DONE - все подзадачи DONE)");
        System.out.println("Статус эпика 2: " + finalEpic2.getStatus() + " (должен быть IN_PROGRESS - подзадача IN_PROGRESS)");
        
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
    }
}