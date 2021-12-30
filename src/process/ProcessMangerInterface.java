package process;

import message.Message;

public interface ProcessMangerInterface {

    void sendMessageToAll(String message);

    void sendMessageToAll(Process senderProcess, String message);

    void sendMessageToAll(Message message);

    void requestElection();

    void performElection();

    void addNewProcess();

    void notifyOthersWithNewProcess();

    String getParsedProcessList();

    void handleReceivedProcessList(String receivedProcessList);
    // will use this to add the received process
    void addNewProcess(int processId);

    void handleVictoryMessage(Message message);
}
