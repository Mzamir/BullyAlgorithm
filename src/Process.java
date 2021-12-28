import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Config.HOST;
import static utils.Config.MESSAGE_TIME_OUT;
import static utils.Constants.ALIVE_MESSAGE;

public class Process {
    private int id;
    private boolean isAlive = true;
    private boolean isCoordinator = false;
    private ServerSocket serverSocket = null;
    private final ProcessMangerInterface mangerInterface;

    public Process(int id, ProcessMangerInterface mangerInterface) {
        this.id = id;
        this.mangerInterface = mangerInterface;
    }

   /* public Process(int port) {
        this.id = port;
    }

    public String sendMessage(Process process, String message) {
        String response = "";
        DataInputStream input;
        DataOutputStream out;
        try (Socket socket = new Socket(HOST, process.getId())) {
            socket.setSoTimeout(MESSAGE_TIME_OUT);
            System.out.println("Seninputg message " + message + " From " + this.getId() + " to " + process.getId());
            input = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            response = input.readUTF();
            System.out.println(" Process " + process.getId() + " is " + response);
            out.flush();
            socket.close();
            out.close();
        } catch (Exception e) {
            System.out.println("IOException" + e.getMessage());
        }

        return response;
    }

    public void handleReceivedMessages() {
        String response = "";
        DataInputStream input;
        DataOutputStream out;
        try {
            initiateServerSocket();
            Socket socket = serverSocket.accept();
            input = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            response = input.readUTF();
            System.out.println(" Process " + response);
            out.flush();
            socket.close();
            out.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startListen() {
        while (isAlive()) {
            initiateServerSocket();
            handleReceivedMessages();
            if (isCoorinputator())
                mangerInterface.sendMessageToAll(ALIVE_MESSAGE);
        }
        System.out.println(this.getId() + " no coorinputator");
        if (!isCoorinputator())
            mangerInterface.requestElection(this);

    }
*/

    public String sendMessage(Process process, String message) {
        try {
            Socket socket = new Socket(HOST, process.getId());
            System.out.println("sending to " + message + " to " + process.getId());
            socket.setSoTimeout(MESSAGE_TIME_OUT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            out.writeUTF(message);
            String response = input.readUTF();
//            process.decodeResponse(response);
            out.flush();
            out.close();
            socket.close();
            return response;
        } catch (Exception e) {
            System.out.println(e + " " + process.getId());
        }
        return null;
    }

    public String receiveAndGiveResponse() {
        try {
            initiateServerSocket();
            serverSocket.setSoTimeout(MESSAGE_TIME_OUT);
            Socket socket = serverSocket.accept();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            String response = input.readUTF();
            System.out.println("received message :  " + response);
            System.out.println("responded with " + response);
            out.writeUTF(response);
            out.flush();
            out.close();
            input.close();
            return response;
        } catch (Exception e) {
            if (!this.isCoordinator())
                mangerInterface.requestElection(this);
        }
        return null;
    }

    void startListen() {
        System.out.println("I'm listening to " + this.getId());
        if (isAlive) {
            if (isCoordinator) {
                initiateServerSocket();
                receiveAndGiveResponse(); ///listen for 2 second and send alive wait indefinitely
                mangerInterface.sendMessageToAll(ALIVE_MESSAGE);
            } else {
                /// when timed out the socket is not closed so we don't need to open it again
                initiateServerSocket();
                receiveAndGiveResponse(); /// wait 3 seconds
            }
        }
        System.out.println(this.getId() + " no coordinator");
        if (isCoordinator)
            mangerInterface.requestElection(this);

    }

    private void initiateServerSocket() {
        if (this.getServerSocket() == null || this.getServerSocket().isClosed()) {
            System.out.println("binding " + this.getId());
            try {
                setServerSocket(new ServerSocket(this.getId()));
//                getServerSocket().setSoTimeout(MESSAGE_TIME_OUT);
            } catch (IOException e) {
                System.out.println("Couldn't establish connection on " + getId());
            }
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
        isCoordinator = coordinator;
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