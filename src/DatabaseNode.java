import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseNode {
    private int tcpPort;
    private Map<Integer, Integer> data;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<Socket> connectedNodes;

    public DatabaseNode(int tcpPort, int initialKey, int initialValue) {
        this.tcpPort = tcpPort;
        this.data = new HashMap<>();
        this.data.put(initialKey, initialValue);
        this.executorService = Executors.newFixedThreadPool(5); // Umożliwia obsługę wielu klientów równocześnie
        this.connectedNodes = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(tcpPort);
            System.out.println("Node started on port " + tcpPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String request;
                while ((request = in.readLine()) != null) {
                    String response = processRequest(request);
                    out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String processRequest(String request) {
        String[] parts = request.split(" ");
        String command = parts[0].toLowerCase();
        System.out.println("Received request: " + request);
        try {
            switch (command) {
                case "set-value":
                    int setKey = Integer.parseInt(parts[1]);
                    int setValue = Integer.parseInt(parts[2]);
                    if (data.containsKey(setKey)) {
                        data.put(setKey, setValue);
                        return "OK";
                    } else {
                        return "ERROR: Key not found";
                    }
                case "get-value":
                    int getKey = Integer.parseInt(parts[1]);
                    if (data.containsKey(getKey)) {
                        return getKey + ":" + data.get(getKey);
                    } else {
                        return "ERROR: Key not found";
                    }
                case "find-key":
                    int findKey = Integer.parseInt(parts[1]);
                    if (data.containsKey(findKey)) {
                        return "FOUND " + InetAddress.getLocalHost().getHostAddress() + ":" + tcpPort;
                    } else {
                        return "ERROR: Key not found";
                    }
                case "get-min":
                    int minKey = -1;
                    int minValue = Integer.MAX_VALUE;
                    for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
                        if (entry.getValue() < minValue) {
                            minKey = entry.getKey();
                            minValue = entry.getValue();
                        }
                    }
                    if (minKey != -1) {
                        return minKey + ":" + minValue;
                    }
                    return "ERROR: No data in the database";
                case "get-max":
                    int maxKey = -1;
                    int maxValue = Integer.MIN_VALUE;
                    for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
                        if (entry.getValue() > maxValue) {
                            maxKey = entry.getKey();
                            maxValue = entry.getValue();
                        }
                    }
                    if (maxKey != -1) {
                        return maxKey + ":" + maxValue;
                    }
                    return "ERROR: No data in the database";
                case "new-record":
                    int newKey = Integer.parseInt(parts[1]);
                    int newValue = Integer.parseInt(parts[2]);
                    data.put(newKey, newValue);
                    return "OK";
                case "terminate":
                    for (Socket nodeSocket : connectedNodes) {
                        nodeSocket.close();
                    }
                    serverSocket.close();
                    executorService.shutdownNow();
                    return "OK";

                default:
                    return "ERROR: Unknown command";
            }
        } catch (Exception e) {
            return "ERROR: Invalid request format or internal error";
        }
    }

    public void connectToNode(String address, int port) {
        try {
            Socket nodeSocket = new Socket(address, port);
            connectedNodes.add(nodeSocket);
            System.out.println("Connected to node at " + address + ":" + port);
        } catch (IOException e) {
            System.err.println("Could not connect to node at " + address + ":" + port);
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java DatabaseNode -tcpport <port> -record <key>:<value> [-connect <address>:<port> ...]");
            System.exit(1);
        }

        int tcpPort = 0;
        int initialKey = 0;
        int initialValue = 0;
        DatabaseNode databaseNode = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-tcpport":
                    tcpPort = Integer.parseInt(args[++i]);
                    break;
                case "-record":
                    String[] record = args[++i].split(":");
                    initialKey = Integer.parseInt(record[0]);
                    initialValue = Integer.parseInt(record[1]);
                    databaseNode = new DatabaseNode(tcpPort, initialKey, initialValue);
                    break;
                case "-connect":
                    if (databaseNode != null) {
                        String[] connectArgs = args[++i].split(":");
                        String connectAddress = connectArgs[0];
                        int connectPort = Integer.parseInt(connectArgs[1]);
                        databaseNode.connectToNode(connectAddress, connectPort);
                    } else {
                        System.out.println("Node must be initialized before connecting.");
                    }
                    break;
            }
        }

        if (databaseNode != null) {
            databaseNode.start();
        } else {
            System.out.println("Failed to initialize the DatabaseNode.");
        }
    }
}
