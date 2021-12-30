package utils;

//import com.google.gson.Gson;

import message.Message;
import process.Process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectMapper {

    public static String mapMessageToString(Message message) {
        return message.getTimeStamp() + ";"
                + message.getProcessId() + ";"
                + message.getMessageTyp() + ";"
                + message.getContent() + ";";
//        return new Gson().toJson(message);
    }

    public static Message mapMessageToObject(String response) {
        String[] temp = response.split(";");
        // Timestamp -> processId -> MessageType -> content
        return new Message(Long.parseLong(temp[0]), Integer.parseInt(temp[1]), temp[2], temp[3]);
//        return new Gson().fromJson(response, Message.class);
    }

    public static String mapProcessListToString(Process myProcess, List<Process> processList) {
        StringBuilder response = new StringBuilder();
        response.append(myProcess.getId());
        for (Process p : processList) {
            if (p.getId() != myProcess.getId())
                response.append(",").append(p.getId());
        }
        return response.toString();
    }

    public static List<Integer> mapProcessStringToList(String response) {
        String[] temp = response.split(",");
        return Stream.of(temp).map(Integer::parseInt).collect(Collectors.toList());
    }
}
