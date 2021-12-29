import process.ProcessManger;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        ProcessManger processManger = new ProcessManger();
        processManger.initiate();
    }
}
