package kanban.manager;

import kanban.model.Node;
import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> first;
    private Node<Task> last;
    private Map<Integer, Node<Task>> idToNode;

    public InMemoryHistoryManager() {
        idToNode = new HashMap<>();
    }

    public void linkLast(Task task) {
        Node<Task> oldLast = last;
        Node<Task> newLast = new Node<>(oldLast, task, null);
        last = newLast;
        if(oldLast == null){
            first = last;
        } else {
            oldLast.next = newLast;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentTask = first;
        while(currentTask != null){
            tasks.add(currentTask.data);
            currentTask = currentTask.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        if(prev == null) {
            first = next;
            if(next != null){
                next.prev = null;
            }
        } else {
            prev.next = next;
        }
        if(next == null) {
            last = prev;
            if(prev != null){
                prev.next = null;
            }
        } else {
            next.prev = prev;
        }
        node.data = null;
        node.prev = null;
        node.next = null;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if(idToNode.containsKey(task.getId())) {
            Node<Task> nodeToDelete = idToNode.get(task.getId());
            removeNode(nodeToDelete);
        }
        linkLast(task);
        idToNode.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        Node<Task> nodeToRemove = idToNode.remove(id);
        if(nodeToRemove != null){
            removeNode(nodeToRemove);
        }
    }
}
