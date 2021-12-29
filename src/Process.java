import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Constants.HOST;
import static utils.Constants.MESSAGE_TIME_OUT;
import static utils.Constants.ALIVE_MESSAGE;

public class Process {
    private int id;
    private boolean isAlive = true;
    private boolean isCoordinator = false;
    private ServerSocket serverSocket = null;
    private final ProcessMangerInterface mangerInterface;

    public Process(int id, boolean isAlive, boolean isCoordinator, ProcessMangerInterface mangerInterface) {
        this.id = id;
        this.isAlive = isAlive;
        this.isCoordinator = isCoordinator;
        this.mangerInterface = mangerInterface;
    }

    public Process(int id, ProcessMangerInterface mangerInterface) {
        this.id = id;
        this.mangerInterface = mangerInterface;
    }

    public String sendMessage(Process process, String message) {
        try {
            Socket socket = new Socket(HOST, process.getId()); // Register this on coming process port
            System.out.println("Client connected");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputStream.writeUTF(message);
            outputStream.flush();

            String responseFromServer = serverReader.readLine();
            System.out.println(responseFromServer);

            outputStream.close();
            serverReader.close();
            inputStream.close();
            socket.close();
            System.out.println("Connection closed");
            return responseFromServer;
        } catch (Exception e) {
            System.out.println(e + " " + process.getId());
        }

        return null;
    }

    // Act as socket server
    public String receiveMessage() throws IOException {
        Socket socket = null;
        DataInputStream inputStream = null;
        PrintStream printStream = null;
        try {
            serverSocket.setSoTimeout(MESSAGE_TIME_OUT);
            socket = serverSocket.accept();
            inputStream = new DataInputStream(socket.getInputStream());
            // output data to the client
            printStream = new PrintStream(socket.getOutputStream());
            // read from client
            String messageFromClient = inputStream.readUTF();
            System.out.println(messageFromClient);

            // send to client
            printStream.println("OK");

        } catch (IOException e) {
            System.out.println("Receive Message " + this.getId());
        }
        printStream.close();
        socket.close();
        serverSocket.close();
        inputStream.close();
        System.out.println("Connection closed");
        return null;
    }

    public void startListen() {
        System.out.println("Listening on " + this.getId());
        while (isAlive) {
            initiateServerSocket();
            try {
                receiveMessage();
            }catch (IOException e){
                e.printStackTrace();
            }
            if (isCoordinator) {
                mangerInterface.sendMessageToAll(this, ALIVE_MESSAGE);
            }
        }
        if (!isCoordinator)
            mangerInterface.requestElection(this);

    }

    private void initiateServerSocket() {
        try {
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                serverSocket = new ServerSocket(this.getId());
            }
        } catch (IOException e) {
            System.out.println("Couldn't establish connection on " + getId());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isCoordinator() {
        return isCoordinator;
    }

    public void setCoordinator(boolean coordinator) {
        this.isCoordinator = coordinator;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", isAlive=" + isAlive +
                ", isCoorinputator=" + isCoordinator +
                '}';
    }
}