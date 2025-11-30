package kanban.handler;

import kanban.model.Epic;
import kanban.model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest extends HttpTaskServerTest {

    @Override
    protected int getPort() {
        return 8082;
    }

    @Test
    void shouldReturnAllSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask"))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId(), null, null);
        taskManager.createSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask/" + subtask.getId()))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask/999"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        String subtaskJson = String.format(
            "{\"title\":\"Test\",\"description\":\"Description\",\"epicId\":%d,\"status\":\"NEW\"}",
            epic.getId()
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldReturnTimeConflictWhenCreatingOverlappingSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId(),
                "10:00 01.01.24", "60");
        taskManager.createSubtask(subtask1);
        String subtask2Json = String.format(
            "{\"title\":\"Subtask 2\",\"description\":\"Description\"," +
                    "\"epicId\":%d,\"status\":\"NEW\",\"startTime\":\"10:30 01.01.24\",\"duration\":60}",
            epic.getId()
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId(), null, null);
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask/" + subtask.getId()))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/subtask/999"))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

