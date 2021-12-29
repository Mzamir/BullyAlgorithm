import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static utils.Constants.*;

public class ProcessManger implements ProcessMangerInterface {
    private List<Process> processList = new ArrayList<>();
    private Process coordinatorProcess;
    int initialPort = INITIAL_PORT;


    public ProcessManger() {
    }

    public ProcessManger(int numberOfProcess) {
        for (int i = 1; i <= numberOfProcess; i++)
            processList.add(new Process(++initialPort, this));

        // Initiate myProcess as the coordinator process by selecting highest id
        coordinatorProcess = new Process(INITIAL_PORT, true, true, this);
        coordinatorProcess.startListen();
    }

    public ProcessManger(List<Process> processList) {
        this.processList = new ArrayList<>(processList);
        // Initially myProcess will be the coordinator process
//        myProcess = getCoordinatorProcess();
    }

    /*
     * 1- Initiate all process with unique id
     * 2- Coordinator sends messages to the other process
     * */
    public void run() {
        System.out.println(processList);
        System.out.println("Coordinator Id " + coordinatorProcess.getId());
        // at first coordinator process should send to all other process coordinator message and wait for response
        for (Process process : processList)
            process.startListen();

        sendMessageToAll(coordinatorProcess, COORDINATOR_MESSAGE);
    }


    // get Process with the highest id
    private Process getCoordinatorProcess() {
//        resetCoordinatorFlag();
//        Optional<Process> optionalProcess = processList.stream().filter(Process::isAlive)
//                .max(Comparator.comparingInt(Process::getId));
//        if (optionalProcess.isPresent()) {
//            Process process = optionalProcess.get();
//            process.setCoordinator(true);
//            return process;
//        }
//        return null;
        return processList.stream().filter(Process::isCoordinator).findFirst().get();
    }

    private void resetCoordinatorFlag() {
        processList.stream().filter(Process::isAlive)
                .forEach(process -> process.setCoordinator(false));
    }

    public void addProcess() {
        Process process;
        // if the list is empty, will initiate a process as a coordinator
        if (processList.isEmpty()) {
            process = new Process(INITIAL_PORT, true, true, this);
            process.startListen();
            System.out.println("Process on " + process.getId() + " is the coordinator");
        } else {
            process = new Process(processList.get(processList.size() - 1).getId() + 1, this);
            process.sendMessage(getCoordinatorProcess(), REGISTER_MESSAGE);
        }
        processList.add(process);

    }

    @Override
    public void sendMessageToAll(Process senderProcess, String message) {
        processList.stream().filter(process -> process.getId() != senderProcess.getId())
                .forEach(process -> senderProcess.sendMessage(process, message));
    }

    @Override
    public void sendMessageToAll(String message) {
    }

    @Override
    public void requestElection(Process process) {
        System.out.println("Election requested");
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }
}
