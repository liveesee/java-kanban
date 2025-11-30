package kanban.handler;

import kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends HttpTaskServerTest {
    @Override
    protected int getPort() {
        return 8083;
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description", null, null);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/history"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}

