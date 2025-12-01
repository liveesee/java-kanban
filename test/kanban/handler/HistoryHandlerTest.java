package kanban.handler;

import com.google.gson.reflect.TypeToken;
import kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest extends HttpTaskServerTest {
    @Override
    protected int getPort() {
        return 8083;
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", null, null);
        Task task2 = new Task("Task 2", "Description 2", null, null);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/history"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(response.body(), listType);
        assertNotNull(history);
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(t -> t.getId() == task1.getId()));
        assertTrue(history.stream().anyMatch(t -> t.getId() == task2.getId()));
    }
}

