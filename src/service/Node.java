package service;

import model.Task;

class Node {
    Node next;
    Task data;
    Node prev;

    Node(Task task) {
        data = task;
        next = null;
        prev = null;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getData() {
        return data;
    }
}