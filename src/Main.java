import process.ProcessManger;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        for (int i = 0; i < 3; i++) {
//            new Thread(() -> {
        ProcessManger processManger = new ProcessManger();
        processManger.initiate();
//            }).start();
//            Thread.sleep(1000);
//        }
    }
}
