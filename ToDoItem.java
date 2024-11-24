/**
 * Author: Emily Lai 100825007
 */

import java.io.Serializable;

public class ToDoItem implements Serializable {
    private String title; // represents the task's title
    private String description; // represents the task's description
    private String clientID; // represents the client that created the task

    // Constructor for ToDoItem
    public ToDoItem(String title, String description, String clientID) {
        this.title = title;
        this.description = description;
        this.clientID = clientID;
    }

    // Getters and setters for ToDoItem
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientID() {
        return clientID;
    }
}
