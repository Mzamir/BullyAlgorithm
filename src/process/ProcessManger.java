package process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static utils.Constants.*;

public class ProcessManger implements ProcessMangerInterface {
    private List<Process> processList = new ArrayList<>();
    private Process coordinatorProcess;
    private Process myProcess;
    int initialPort = INITIAL_PORT;


    public ProcessManger() {
        myProcess = new Process(INITIAL_PORT, this);
    }

    public void initiate() {
        if (sendRegistrationMessage() == null) {
            // means that there is no coordinator
            // Register current process as the coordinator
            myProcess.setCoordinator(true);
            myProcess.startListen();
        } else {
            myProcess.startListen();
        }
    }


    // sending registration message to the coordinator
    private String sendRegistrationMessage() {
        return null;
    }

    // get process.Process with the highest id
    private Process getCoordinatorProcess() {
        return processList.stream().filter(Process::isCoordinator).findFirst().get();
    }

    private void performElection() {
        resetCoordinatorFlag();
        Optional<Process> optionalProcess = processList.stream().filter(process.Process::isAlive)
                .max(Comparator.comparingInt(process.Process::getId));
        if (optionalProcess.isPresent()) {
            process.Process process = optionalProcess.get();
            process.setCoordinator(true);
            coordinatorProcess = process;
            return;
        }
        coordinatorProcess = null;
    }

    private void resetCoordinatorFlag() {
        processList.stream().filter(Process::isAlive)
                .forEach(process -> process.setCoordinator(false));
    }

    public void addProcess() {
        System.out.println("**ADDING**");
        Process process;
        // if the list is empty, will initiate a process as a coordinator
        if (processList.isEmpty()) {
            process = new Process(INITIAL_PORT, true, true, this);
            System.out.println("Process on " + process.getId() + " is the coordinator");
        } else {
            process = new Process(processList.get(processList.size() - 1).getId() + 1, this);
            coordinatorProcess = getCoordinatorProcess();
            process.sendMessage(coordinatorProcess, REGISTER_MESSAGE);
            coordinatorProcess.setServerSocket(null);
            makeProcessReceiveMessage(coordinatorProcess, process);

        }
        processList.add(process);

    }

    private void makeProcessReceiveMessage(Process receiver, Process sender) {
        try {
            receiver.receiveMessage();
        } catch (IOException e) {
            /*
             1- if the process we want it to receive the message is the coordinator
             2- Request election
             */
            if (receiver.isCoordinator())
                requestElection(sender);
        }
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
        System.out.println("*************************");
        System.out.println("*************************");
        performElection();

        if (coordinatorProcess == null)
            System.out.println("no coordinator elected");
        else {
            System.out.println("Process on " + process.getId() + " elected as coordinator");
        }

    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
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
}
