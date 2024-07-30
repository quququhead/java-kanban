package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        if (!(history.isEmpty())) {
            Node node = head;
            for (int i = 0; i < history.size(); i++) {
                historyList.add(node.getData());
                node = node.getNext();
            }
        }
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            Node node = new Node(task);
            linkLast(node);
            history.put(task.getId(), node);
        }
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    private void linkLast(Node node) {
        if (history.isEmpty()) {
            head = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
        }
        tail = node;
    }

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev == null && next == null) {
            head = null;
            tail = null;
        } else if (prev == null) {
            next.setPrev(null);
            head = next;
        } else if (next == null) {
            prev.setNext(null);
            tail = prev;
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }
}