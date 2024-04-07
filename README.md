# Distributed-database-nexus
## Descrpiton

This documentation details the implementation and usage of a simple distributed database system, comprising two primary components: `DatabaseNode` (server) and `DatabaseClient` (client).

## Getting started
### Dependencies 
Java JDK 1.8 is required to compile and run the project.

# 1. Network Organization
## 1.1 Network Structure
The network comprises database nodes ('DatabaseNode') and clients ('DatabaseClient'). Each node acts as a TCP server, listening on a specified port to handle client requests. Data is stored in a map within each node, facilitating basic data operations.

## 1.2 Database Nodes
Database nodes are TCP servers that listen on a specified port and handle client requests. Each node stores data in a map and supports basic operations on this data.

## 1.3 Clients
Client applications connect to a database node to perform data operations. Each node can be connected to other nodes and handle requests from clients.

# 2. Launching a Node
## Initialization
A node is launched with a specified TCP port, initial key, and value. For example:
```bash
java DatabaseNode -tcpport <port> -initialKey <initialKey> -initialValue <initialValue>
javac DatabaseNode.java DatabaseClient.java
```
# 3. Communication Protocol
## Format
Messages are textual, with commands and parameters separated by spaces. For instance, **'set-value 1 100'** sets the value **'100'** for key **'1'**.

# 4. Implemented Functions
## 4.1 Node Requests Handling
Nodes receive requests from clients, process them, and send back a response.

## 4.2 Supported Client Requests
- **get-value**: Retrieves the value for a given key. Returns the value or NOT FOUND if the key does not exist.
- **set-value**: Extracts the key and value from the request. Updates the value in the data map if the key exists, otherwise returns ERROR: Key not found.
- **new-record**: Adds a new key and value to the data map. Returns OK, even if the key already exists (overwrites the value).
- **get-min**: Searches the data map for the smallest value and returns <key>:<value> if successful. Returns ERROR: No data in the database otherwise.
- **get-max**: Searches the data map for the largest value and returns <key>:<value> if successful. Returns ERROR: No data in the database otherwise.
- **find-key**: Searches for the node address and port storing the record with the given key. Returns <address>:<port> if found, or ERROR: Key not found otherwise.

# 5. Client Requests Handling
## 5.1 Single Client Handling
Each node can handle requests from one client at a time, facilitated by a single socket and client connection.

# 6. Handling Multiple Clients
## 6.1 Concurrent Clients
Implemented using multithreading **`(ExecutorService)`** to handle multiple clients simultaneously.

# 7. Communication Description
## 7.1 Connection Initiation
Clients initiate a connection using the IP address and TCP port to connect to a node. The node listens on the specified port and accepts incoming connections and requests from clients.

## 7.2 Data Transmission
Data is transmitted using `InputStream` and `OutputStream`. Clients send requests, and servers respond.

## 7.3 Request Processing
Upon receiving a request, the server parses the text, extracting the command and arguments.

## 7.4 Operation Execution
Based on the command, the server performs the appropriate data operation, such as reading, writing, or deleting.

## 7.5 Network Errors
Network errors, such as connection loss, result in error messages from the server or client.

## 7.6 Exception Handling
Internal exceptions are caught and handled, returning appropriate error messages.

# 8. DatabaseClient - Detailed Description
## 8.1 `-gateway` Argument Analysis
Determines the server's IP address and port.

## 8.2  `-operation ` Argument Analysis
Creates the command to be sent to the server.

## 8.3 Connection Establishment
Establishes a socket connection with the server **`(DatabaseNode)`** using the provided IP address and port.

## 8.4 Command Sending
Sends the command to the server.

## 8.5 Response Waiting and Reading
Waits for and reads the server's response.

## 8.6 Communication Error Handling
Handles `UnknownHostException` and `IOException` for network communication issues.

# 9. DatabaseNode - Detailed Description
## 9.1 Command-Line Argument Analysis
Analyzes arguments for the TCP port and initial key-value pair.

## 9.2 Server Initialization
Initializes the server socket, thread pool for client handling, and data storage.

## 9.3 Incoming Connection Listening
Listens for incoming client connections.

## 9.4 Client Handling
Creates a new thread for each client connection.

## 9.5 Request Reading
Reads requests from clients.

## 9.6 Request Processing
Processes the request based on the command (e.g., `get-value`, `set-value`, `new-record`).

## 9.7 Response Sending
Sends the response back to the client.

## 9.8 Supported Commands
Includes commands for retrieving, setting, and deleting values; finding keys; and calculating minimum and maximum values.

## 9.9 connectToNode Method
Allows a node to connect with other nodes, expanding its network.

## 9.10 Safe Connection and Server Shutdown

# 10. Compilation and Installation

## 10.1 Terminal Access
Open a terminal in the directory containing the downloaded files.

## 10.2 Compilation
In the terminal, compile both files with:

```bash
javac DatabaseNode.java DatabaseClient.java
```

## 10.3 Node Launch
Launch the database node with:

```bash
java DatabaseNode -tcpport <port> -record <key>:<value> [-connect <address>:<port> ...]
```

## 10.4 Client Launch
In another terminal window, launch the client with:

```bash
java DatabaseClient -gateway <address>:<port> -operation "<operation>"
```

## 10.5 Batch Files for Automation
To streamline the process of compiling and running the DatabaseNode and DatabaseClient, you can use Windows batch (.bat) files. 

### 10.5.1 Compilation and Execution Script
This batch file compiles the Java files and then starts the DatabaseNode and DatabaseClient programs with the specified arguments.

### 10.5.2 DatabaseNode Launch Script
This script is specifically designed to launch the DatabaseNode with pre-defined parameters and then sequentially start DatabaseClient instances with different operations.






