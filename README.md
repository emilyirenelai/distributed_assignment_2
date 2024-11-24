# distributed_assignment_2

The ToDo List application is a collaborative task manager that enables users to add, edit, and remove tasks in a shared environment. Built using Java-RMI, it supports multiple clients connecting to a central ToDoListServer, where all connected users can view and interact with each other's tasks. Each task is represented by a custom ToDoListItem model, designed to encapsulate a title, description, and the ClientID of the creator. The application includes a refresh button to retrieve the most up-to-date tasks, reflecting changes made by others in real time. Furthermore, it ensures graceful disconnection, removing tasks associated with a client when they disconnect via the application window or a signal handler, and logs the event on the server.

