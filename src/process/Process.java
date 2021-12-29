package process;

import message.Message;
import message.MessageMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Constants.*;

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
        try {
            this.serverSocket = new ServerSocket(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Process(int id, ProcessMangerInterface mangerInterface) {
        this.id = id;
        this.mangerInterface = mangerInterface;
    }

    // act as a client to send message
    public void sendMessage(Process process, Message message) {
        try {
            Socket socket = new Socket(HOST, process.getId());
            socket.setSoTimeout(MESSAGE_TIME_OUT);
            System.out.println("Sending " + message + " from " + this.getId() + " to " + process.getId());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Send message to the process
            outputStream.writeUTF(MessageMapper.parseToString(message));
            // Get response
            String response = inputStream.readUTF();
            handleReceivedMessage(response);

            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + " on " + process.getId());
        }
    }

    // Act as server to receive message
    public Message receiveMessage() {
        try {
            serverSocket.setSoTimeout(MESSAGE_TIME_OUT);
            Socket socket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            String messageFromClient = inputStream.readUTF();
            System.out.println(this.getId() + " Received " + messageFromClient);
            Message message = handleReceivedMessage(messageFromClient);
            System.out.println(this.getId() + " Response is " + message);
            outputStream.writeUTF(MessageMapper.parseToString(message));
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return MessageMapper.parseToObject(messageFromClient);
        } catch (IOException e) {
            if (!this.isCoordinator) {
                System.out.println(this.getId() + " no responding will be deactivated");
                this.isAlive = false;
            }
        }
        return null;
    }

    private Message handleReceivedMessage(String messageFromClient) {
        Message message = MessageMapper.parseToObject(messageFromClient);
        if (message.getMessageTyp().equals(REGISTER_MESSAGE)) {
            mangerInterface.addNewProcess();
        } else if (message.getMessageTyp().equals(ELECTION_MESSAGE)) {
            mangerInterface.requestElection();
        }
        return new Message(this.getId(), message.getMessageTyp(), messageFromClient);
    }

    public void startListen() {
        while (isAlive) {
            initiateServerSocket();
            receiveMessage();

            if (isCoordinator)
                mangerInterface.sendMessageToAll(this, ALIVE_MESSAGE);
        }
        // if not alive and not coordinator
        // send election request
        if (!isCoordinator)
            mangerInterface.requestElection(this);

    }

    public void initiateServerSocket() {
        try {
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                serverSocket = new ServerSocket(this.getId());
                System.out.println("Listening on " + this.getId());
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
        return "process.Process{" +
                "id=" + id +
                ", isAlive=" + isAlive +
                ", isCoorinputator=" + isCoordinator +
                '}';
    }
}