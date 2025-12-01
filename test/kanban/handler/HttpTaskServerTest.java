package kanban.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanban.HttpTaskServer;
import kanban.manager.HistoryManager;
import kanban.manager.InMemoryHistoryManager;
import kanban.manager.InMemoryTaskManager;
import kanban.manager.TaskManager;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class HttpTaskServerTest {
    protected HttpTaskServer server;
    protected TaskManager taskManager;
    protected HttpClient httpClient;
    protected Gson gson;
    protected int port;

    protected abstract int getPort();

    @BeforeEach
    void setUp() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        this.port = getPort();
        
        server = new HttpTaskServer(taskManager, port);
        server.start();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        httpClient = HttpClient.newHttpClient();
        gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new GsonAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new GsonAdapters.DurationAdapter())
            .create();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }
}

