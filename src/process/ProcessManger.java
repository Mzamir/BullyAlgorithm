package process;

import message.Message;
import utils.ObjectMapper;

import static message.MessagesTypes.*;

import java.util.*;
import java.util.stream.IntStream;

import static utils.Constants.*;

public class ProcessManger implements ProcessMangerInterface {
    private List<Process> processList = new ArrayList<>();
    private Process myProcess;


    public ProcessManger() {
        myProcess = new Process(INITIAL_PORT, this);
    }

    public void initiate() {
        String coordinatorResponse = sendRegistrationMessage();
        if (coordinatorResponse == null) {
            // means that there is no coordinator
            // Register current process as the coordinator
            System.out.println("There is no coordinator");
            myProcess.setCoordinator(true);
        }
        myProcess.startListen();
    }

    // sending registration message to the coordinator
    private String sendRegistrationMessage() {
        Message message = new Message(new Date().getTime(), myProcess.getId(), REGISTER_MESSAGE, REGISTER_MESSAGE);
        return myProcess.sendMessage(myProcess, message);
    }

    // get process.Process with the highest id
    private Process getCoordinatorProcess() {
        return processList.stream().filter(Process::isCoordinator).findFirst().get();
    }

    private void resetCoordinatorFlag() {
        processList.stream().filter(Process::isAlive)
                .forEach(process -> process.setCoordinator(false));
    }


    private void makeProcessReceiveMessage(Process receiver, Process sender) {
//        try {
//            receiver.receiveMessage();
//        } catch (IOException e) {
//            /*
//             1- if the process we want it to receive the message is the coordinator
//             2- Request election
//             */
//            if (receiver.isCoordinator())
//                requestElection(sender);
//        }
    }

    @Override
    public void sendMessageToAll(Message message) {
        processList.stream().filter(process -> process.getId() != message.getProcessId())
                .forEach(process -> myProcess.sendMessage(process,
                        new Message(process.getId(), ALIVE_MESSAGE, message.getContent())));
    }

    @Override
    public void sendMessageToAll(Process senderProcess, String message) {
        processList.stream()/*.filter(process -> process.getId() != senderProcess.getId())*/
                .forEach(process -> senderProcess.sendMessage(process,
                        new Message(senderProcess.getId(), ALIVE_MESSAGE, message)));
    }

    @Override
    public void sendMessageToAll(String message) {
        processList.forEach(process -> myProcess.sendMessage(process,
                new Message(new Date().getTime(), myProcess.getId(), ALIVE_MESSAGE, message)));
    }

    /*
     * send election message to all processes
     * set myProcess as a coordinator
     * if one of the processes respond with VICTORY will reset coordinator flag
     *
     * */
    @Override
    public void requestElection() {
        System.out.println("Election requested");
        System.out.println("*************************");
        sendMessageToAll(new Message(new Date().getTime(), myProcess.getId(), ELECTION_MESSAGE, String.valueOf(myProcess.getId())));
        myProcess.setCoordinator(true);
    }

    @Override
    public void performElection() {
        resetCoordinatorFlag();
        Optional<Process> optionalProcess = processList.stream().filter(process.Process::isAlive)
                .max(Comparator.comparingInt(process.Process::getId));
        if (optionalProcess.isPresent()) {
            process.Process process = optionalProcess.get();
            process.setCoordinator(true);
        }
    }

    @Override
    public void addNewProcess() {
        System.out.println("**ADDING**");
        Process process;
        // if the list is empty, will initiate a process as a coordinator
        if (processList.isEmpty()) {
            process = new Process(INITIAL_PORT + 1);
        } else {
            process = new Process(processList.get(processList.size() - 1).getId() + 1);
//            sendMessageToAll(new Message(new Date().getTime(), myProcess.getId(), ADD_MESSAGE, String.valueOf(process.getId())));
        }
        processList.add(process);
        System.out.println("process added " + process.getId());
    }

    @Override
    public void notifyOthersWithNewProcess() {
        IntStream.range(0, processList.size() - 1).forEach(i -> myProcess.sendMessage(processList.get(i),
                new Message(new Date().getTime(), myProcess.getId(), ADD_MESSAGE, String.valueOf(processList.get(i).getId()))));
    }

    @Override
    public void handleReceivedProcessList(String receivedProcessList) {
        List<Integer> processIdsList = ObjectMapper.mapProcessStringToList(receivedProcessList);
        List<Process> receivedProcesses = new ArrayList<>();
        processIdsList.forEach(id -> receivedProcesses.add(new Process(id)));
        myProcess.setId(receivedProcesses.get(receivedProcesses.size() - 1).getId());
        receivedProcesses.remove(receivedProcesses.size() - 1);
        setProcessList(receivedProcesses);
    }

    @Override
    public void addNewProcess(int processId) {
        processList.add(new Process(processId));
    }

    @Override
    public void handleVictoryMessage(Message message) {
        int winnerProcessId = message.getProcessId();
        System.out.println(winnerProcessId + " Won the election ");
        this.processList.removeIf(process -> process.getId() == winnerProcessId);
        myProcess.setId(winnerProcessId);
        myProcess.setCoordinator(true);
        myProcess.setAlive(true);
    }

    @Override
    public String getParsedProcessList() {
        return ObjectMapper.mapProcessListToString(this.myProcess, processList);
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }
}
