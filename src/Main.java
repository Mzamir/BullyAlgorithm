import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Constants.INITIAL_PORT;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        ProcessManger processManger = new ProcessManger();
        System.out.println("Enter number of processes");
        int numberOfProcess = sc.nextInt();
        for (int i = 0; i < numberOfProcess; i++) {
            processManger.addProcess();
            Thread.sleep(1000);
        }
//        System.out.println("Enter number of processes");
//        int numberOfProcess = sc.nextInt();
//

//        ProcessManger processManger = new ProcessManger(numberOfProcess);
//        processManger.run();
    }
}
