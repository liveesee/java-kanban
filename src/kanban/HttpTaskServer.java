package kanban;

import com.sun.net.httpserver.HttpServer;
import kanban.handler.*;
import kanban.manager.HistoryManager;
import kanban.manager.Managers;
import kanban.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private static final int DEFAULT_PORT = 8080;

    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        initHandlers();
    }

    public HttpTaskServer() throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        this.taskManager = Managers.getDefault(historyManager);
        this.httpServer = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);
        initHandlers();
    }

    private void initHandlers() {
        httpServer.createContext("/task", new TaskHandler(taskManager));
        httpServer.createContext("/epic", new EpicHandler(taskManager));
        httpServer.createContext("/subtask", new SubtaskHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP Task Server запущен на порту " + httpServer.getAddress().getPort());
    }

    public void stop(int delay) {
        httpServer.stop(delay);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
