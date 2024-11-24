/**
 * Author: Emily Lai 100825007
 */

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ToDoListServer extends UnicastRemoteObject implements ToDoListInterface {
    private final List<ToDoItem> tasks;
    public ToDoListServer() throws RemoteException {
        tasks = new ArrayList<>();
    }

    // adds a new task to the list, synchronized to prevent concurrent modification
    public synchronized void addTask(String title, String description, String clientID) throws RemoteException {
        tasks.add(new ToDoItem(title, description, clientID));
    }

    // updates an existing task in the list, synchronized to prevent concurrent modification
    public synchronized void updateTask(String oldTitle, String newTitle, String newDescription, String clientID)
            throws RemoteException {
        for (ToDoItem task : tasks) {
            if (task.getTitle().equals(oldTitle) && task.getClientID().equals(clientID)) {
                task.setTitle(newTitle);
                task.setDescription(newDescription);
                break;
            }
        }
    }

    // removes a task from the list, synchronized to prevent concurrent modification
    public synchronized void removeTask(String title) throws RemoteException {
        tasks.removeIf(task -> task.getTitle().equals(title));
    }

    // returns a list of all tasks, synchronized to prevent concurrent modification
    public synchronized List<ToDoItem> getTasks() throws RemoteException {
        return new ArrayList<>(tasks);
    }

    // disconnects a client from the server, according to the clientID, and removes all tasks associated with that client, synchronized to prevent concurrent modification
    public synchronized void disconnectClient(String clientID) throws RemoteException {
        tasks.removeIf(task -> task.getClientID().equals(clientID));
        System.out.println("Client " + clientID + " disconnected. Tasks removed.");
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ToDoList", new ToDoListServer());
            System.out.println("To-Do List Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
