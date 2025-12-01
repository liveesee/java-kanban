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

public class PrioritizedHandlerTest extends HttpTaskServerTest {
    @Override
    protected int getPort() {
        return 8084;
    }

    @Test
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", "10:00 01.01.24", "60");
        Task task2 = new Task("Task 2", "Description", "12:00 01.01.24", "60");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/prioritized"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> prioritizedTasks = gson.fromJson(response.body(), listType);
        assertNotNull(prioritizedTasks);
        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.stream().anyMatch(t -> t.getId() == task1.getId()));
        assertTrue(prioritizedTasks.stream().anyMatch(t -> t.getId() == task2.getId()));
        assertEquals(task1.getId(), prioritizedTasks.get(0).getId());
        assertEquals(task2.getId(), prioritizedTasks.get(1).getId());
    }
}

