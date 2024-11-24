/**
 * Author: Emily Lai 100825007
 */

 import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ToDoListInterface extends Remote {
    void addTask(String title, String description, String clientID) throws RemoteException; // adds a new task to the list
    void updateTask(String oldTitle, String newTitle, String newDescription, String clientID) throws RemoteException; // updates an existing task in the list
    void removeTask(String title) throws RemoteException; // removes a task from the list
    List<ToDoItem> getTasks() throws RemoteException; // returns a list of all tasks
    void disconnectClient(String clientID) throws RemoteException; // disconnects a client from the server
}
