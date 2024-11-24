/**
 * Author: Emily Lai 100825007
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

/**
 * Client class for the collaborative to-do list manager.
 */
public class ToDoListClient {
    private final String clientID; // Unique identifier for this client
    private ToDoListInterface toDoList; // remote interface for interacting with the server

    public ToDoListClient(String host) {
        clientID = UUID.randomUUID().toString(); //  generate a unique client ID
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            toDoList = (ToDoListInterface) registry.lookup("ToDoList");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> { // ensures that the client disconnects from the server when the program exits
                try {
                    toDoList.disconnectClient(clientID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // shows the GUI for the to-do list manager
    public void showGUI() {
        JFrame frame = new JFrame("Collaborative To-Do List Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        DefaultListModel<String> taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);
        JScrollPane scrollPane = new JScrollPane(taskList);

        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JButton addButton = new JButton("Add Task");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener((ActionEvent e) -> {
            try {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                if (!title.isEmpty()) {
                    toDoList.addTask(title, description, clientID);
                    updateTaskList(taskListModel);
                    titleField.setText("");
                    descriptionField.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        refreshButton.addActionListener((ActionEvent e) -> updateTaskList(taskListModel));

        taskList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    String title = selectedTask.split(":")[0].trim();
                    openEditWindow(title, taskListModel);
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(addButton);
        inputPanel.add(refreshButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.NORTH);

        new Timer(2000, (ActionEvent e) -> updateTaskList(taskListModel)).start();
        updateTaskList(taskListModel);

        frame.setVisible(true);
    }

    // the refresh button functionality
    private void updateTaskList(DefaultListModel<String> taskListModel) {
        try {
            List<ToDoItem> tasks = toDoList.getTasks();
            taskListModel.clear();
            for (ToDoItem task : tasks) {
                taskListModel.addElement(task.getTitle() + ": " + task.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // when you click on a task, it opens a new window to edit the task
    private void openEditWindow(String oldTitle, DefaultListModel<String> taskListModel) {
        try {
            List<ToDoItem> tasks = toDoList.getTasks();
            ToDoItem task = tasks.stream().filter(t -> t.getTitle().equals(oldTitle) && t.getClientID().equals(clientID)).findFirst().orElse(null);
            if (task == null) return;

            JFrame editFrame = new JFrame("Edit Task");
            editFrame.setSize(300, 200);

            // create the text fields and buttons
            JTextField titleField = new JTextField(task.getTitle());
            JTextArea descriptionArea = new JTextArea(task.getDescription());
            JButton doneButton = new JButton("Done");
            JButton removeButton = new JButton("Remove Task");

            // button actions
            doneButton.addActionListener((ActionEvent e) -> {
                try {
                    toDoList.updateTask(oldTitle, titleField.getText().trim(), descriptionArea.getText().trim(), clientID);
                    updateTaskList(taskListModel);
                    editFrame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            removeButton.addActionListener((ActionEvent e) -> {
                try {
                    toDoList.removeTask(oldTitle);
                    updateTaskList(taskListModel);
                    editFrame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            editFrame.setLayout(new BorderLayout());
            editFrame.add(titleField, BorderLayout.NORTH);
            editFrame.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(doneButton);
            buttonPanel.add(removeButton);

            editFrame.add(buttonPanel, BorderLayout.SOUTH);
            editFrame.setVisible(true); // show the edit window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        ToDoListClient client = new ToDoListClient(host);
        client.showGUI();
    }
}
