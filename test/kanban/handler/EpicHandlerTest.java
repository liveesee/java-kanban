package kanban.handler;

import com.google.gson.reflect.TypeToken;
import kanban.model.Epic;
import kanban.model.Subtask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest extends HttpTaskServerTest {

    @Override
    protected int getPort() {
        return 8081;
    }

    @Test
    void shouldReturnAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        
        Type listType = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> epics = gson.fromJson(response.body(), listType);
        assertNotNull(epics);
        assertEquals(2, epics.size());
        assertTrue(epics.stream().anyMatch(e -> e.getId() == epic1.getId()));
        assertTrue(epics.stream().anyMatch(e -> e.getId() == epic2.getId()));
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
        
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(responseEpic);
        assertEquals(epic.getId(), responseEpic.getId());
        assertEquals(epic.getTitle(), responseEpic.getTitle());
        assertEquals(epic.getDescription(), responseEpic.getDescription());
        assertEquals(epic.getStatus(), responseEpic.getStatus());
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
            "{\"id\":%d,\"title\":\"Updated\",\"description\":\"Description\"}", epic.getId());
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
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId(), null, null);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/epic/" + epic.getId() + "/subtasks"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), listType);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subtask1.getId()));
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subtask2.getId()));
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

