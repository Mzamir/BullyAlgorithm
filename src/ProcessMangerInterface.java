public interface ProcessMangerInterface {

    void sendMessageToAll(String message);

    void requestElection(Process process);
}
