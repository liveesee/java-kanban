package kanban.handler;

import kanban.model.Epic;
import kanban.model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest extends HttpTaskServerTest {

    @Override
    protected int getPort() {
        return 8081;
    }

    @Test
    void shouldReturnAllEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        taskManager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/" + epic.getId()))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/999"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        String epicJson = "{\"title\":\"Test\",\"description\":\"Description\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(epicJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Original", "Description");
        taskManager.createEpic(epic);
        String updatedJson = String.format(
            "{\"id\":%d,\"title\":\"Updated\",\"description\":\"Description\"}",
            epic.getId()
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", taskManager.getEpicById(epic.getId()).getTitle());
    }

    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId(), null, null);
        taskManager.createSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/" + epic.getId() + "/subtasks"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnNotFoundWhenGettingSubtasksOfNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/999/subtasks"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        taskManager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/" + epic.getId()))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/999"))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

