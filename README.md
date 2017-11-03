# Distributed Systems Assignment 2
### Dean Gaffney - 20067423

### Multithreaded server client application for showing student grades

# Notes:
## The database connection details may need to be changed to suit your WAMP setup
## These can be found in the following package src/com/dist/db/ConnectionPool.java
## The username and passwords are constant fields at the top of the file, change them there if needs be.

# Features
 - Thread pool for efficent reuse of threads after a client has closed their connection with the server (using java 8 ExecutorService)
 - JDBC connection pool using the c3p0 library for efficent reuse of JDBC connections (first query is slow as the pool is being set up, all queries after are fast). This is implemented in a singleton pattern which has been modified to be thread safe, using built in class contract synchronization provided by java.
 - If a user enters the wrong student id , the socket is closed and the stream resources are freed to allow the OS to reclaim the thread
 - If a user enters a wrong module, the server will present them with a list of available modules so they may retry.
 - A client can close their program, this will terminate the program and free resources on the server.
 - Synchronized updating of the server JTextArea (debateable weather JTextArea is thread safe)
 - Student and Module models used to format the results from the database and display the correct overall grade to the user
 - Overall grade is calculated in SQL and the response includes all neccessary fields stated in the specification, this was done with a left join on the students and modulegrades table.
 - Client/ Server address appear on client and server windows.


