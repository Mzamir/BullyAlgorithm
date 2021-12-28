import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static utils.Constants.COORDINATOR_MESSAGE;

public class ProcessManger implements ProcessMangerInterface {
    private List<Process> processList;
    private Process myProcess;

    public ProcessManger() {
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
    public void initiate() {
        this.myProcess = getCoordinatorProcess();
        this.myProcess.setCoordinator(true);

        System.out.println(processList);
        System.out.println("Coordinator Id " + myProcess.getId());
        for (Process process : processList)
            process.startListen();
//        sendMessageToAll(COORDINATOR_MESSAGE);
    }


    // get Process with the highest id
    private Process getCoordinatorProcess() {
        Optional<Process> optionalProcess = processList.stream()
                .filter(Process::isAlive)
                .max(Comparator.comparingInt(Process::getId));
        if (optionalProcess.isPresent()) {
            Process process = optionalProcess.get();
            process.setCoordinator(true);
            return process;
        }
        return null;
    }


    private void addProcess(Process process) throws NullPointerException {
        if (process == null)
            throw new NullPointerException();
        this.addProcess(process);
        this.myProcess = getCoordinatorProcess();
    }

    @Override
    public void sendMessageToAll(String message) {
        for (Process process : processList) {
            if (myProcess.getId() != process.getId())
                myProcess.sendMessage(process, message);
        }
    }

    @Override
    public void requestElection(Process process) {
        System.out.println("Election requested");
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }
}
