import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Config.HOST;

public class MessagingManager {

    private String sendMessage(int port ,String message, int timeOut) {
        try {
            Socket socket = new Socket(HOST, port);
            socket.setSoTimeout(timeOut);
            System.out.println("Send message to " + port);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(message);
            String reply = input.readUTF();
            socket.close();
            output.close();
            return reply;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String receiveMessage(int port ,String message , int timeOut){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
//            Socket
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
