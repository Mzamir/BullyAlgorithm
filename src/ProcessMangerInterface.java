public interface ProcessMangerInterface {

    void sendMessageToAll(String message);

    void sendMessageToAll(Process senderProcess ,String message);

    void requestElection(Process process);
}
