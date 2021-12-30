package process;

import static message.MessagesTypes.*;

import message.Message;
import utils.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static utils.Constants.*;

public class Process {
    private int id;
    private boolean alive = true;
    private boolean coordinator = false;
    private ServerSocket serverSocket = null;
    private ProcessMangerInterface mangerInterface;


    public Process(int id) {
        this.id = id;
    }

    public Process(int id, boolean alive, boolean coordinator, ProcessMangerInterface mangerInterface) {
        this.id = id;
        this.alive = alive;
        this.coordinator = coordinator;
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
    public String sendMessage(Process process, Message message) {
        try {
            Socket socket = new Socket(HOST, process.getId());
            socket.setSoTimeout(MESSAGE_TIME_OUT);
            System.out.println("Sending " + ObjectMapper.mapMessageToString(message) + " from " + this.getId() + " to " + process.getId());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Send message to the process
            outputStream.writeUTF(ObjectMapper.mapMessageToString(message));
            // Get response
            String response = inputStream.readUTF();
            handleResponseOnSendMessage(response);

            // Close connection
            outputStream.flush();
            outputStream.close();
            socket.close();
            return response;
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + " on " + process.getId());
        }
        return null;
    }

    // Act as server to receive message
    public Message receiveMessage() {
        try {
            serverSocket.setSoTimeout(MESSAGE_TIME_OUT);
            Socket socket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Received message
            String messageFromClient = inputStream.readUTF();
            System.out.println(this.getId() + " Received " + messageFromClient);
            // respond to received message
            Message message = handleReceivedMessage(messageFromClient);
            if (message != null) {
                String messageToClient = ObjectMapper.mapMessageToString(message);
                System.out.println(this.getId() + " Response is " + messageToClient);
                outputStream.writeUTF(messageToClient);
            }
            // Close connection
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return ObjectMapper.mapMessageToObject(messageFromClient);
        } catch (IOException e) {
            if (!this.coordinator) {
                System.out.println(this.getId() + " no responding will be deactivated");
                this.alive = false;
            }
        }
        return null;
    }

    // Generate response based on received message
    private Message handleReceivedMessage(String messageFromClient) {
        Message message = ObjectMapper.mapMessageToObject(messageFromClient);
        Message responseMessage;
        System.out.println("Received " + message.getMessageTyp());
        switch (message.getMessageTyp()) {
            case REGISTER_MESSAGE:
                mangerInterface.addNewProcess();
                mangerInterface.notifyOthersWithNewProcess();
                responseMessage = new Message(new Date().getTime(), this.getId(), LIST_MESSAGE, mangerInterface.getParsedProcessList());
                break;
            case ADD_MESSAGE:
                mangerInterface.addNewProcess(Integer.parseInt(message.getContent()));
                responseMessage = new Message(new Date().getTime(), this.getId(), OK_MESSAGE, OK_MESSAGE);
                break;
            case ELECTION_MESSAGE:
                mangerInterface.performElection();
                responseMessage = new Message(new Date().getTime(), this.getId(), OK_MESSAGE, OK_MESSAGE);
                break;
            case VICTORY_MESSAGE:
                mangerInterface.handleVictoryMessage(message);
                responseMessage = new Message(new Date().getTime(), this.getId(), OK_MESSAGE, OK_MESSAGE);
                break;
            default:
                responseMessage = new Message(new Date().getTime(), this.getId(), OK_MESSAGE, OK_MESSAGE);
        }
        return responseMessage;
    }

    private void handleResponseOnSendMessage(String messageFromClient) {
        Message message = ObjectMapper.mapMessageToObject(messageFromClient);
        System.out.println("Received " + message.getMessageTyp());
        if (LIST_MESSAGE.equals(message.getMessageTyp())) {
            mangerInterface.handleReceivedProcessList(message.getContent());
        }
    }

    public void startListen() {
        while (alive) {
            initiateServerSocket();
            if (!isServerDown()) {
                receiveMessage();
                if (coordinator)
                    mangerInterface.sendMessageToAll(ALIVE_MESSAGE);
            }
        }
        // if not alive and coordinator
        // send election request
        if (!coordinator)
            mangerInterface.requestElection();

    }

    private boolean isServerDown() {
        return this.serverSocket == null || this.serverSocket.isClosed();
    }

    public void initiateServerSocket() {
        if (isServerDown())
            try {
                serverSocket = new ServerSocket(this.getId());
                System.out.println("Listening on " + this.getId());
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
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isCoordinator() {
        return coordinator;
    }

    public void setCoordinator(boolean coordinator) {
        this.coordinator = coordinator;
//        System.out.println(this.getId() + " is now the coordinator");
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
                ", isAlive=" + alive +
                ", isCoordinator=" + coordinator +
                '}';
    }
}