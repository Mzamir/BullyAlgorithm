import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Config.PORT;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter number of processes");
        int numberOfProcess = sc.nextInt();

        if (numberOfProcess < 1)
            return;

        ProcessManger processManger = new ProcessManger();
        List<Process> processList = new ArrayList<>();
        int initialPort = PORT ;
        for (int i = 1; i <= numberOfProcess; i++)
            processList.add(new Process(initialPort++, processManger));
        processManger.setProcessList(processList);
        processManger.initiate();
    }
}
