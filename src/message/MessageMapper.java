package message;

import com.google.gson.Gson;

public class MessageMapper {

    public static String parseToString(Message message) {
        return new Gson().toJson(message);
    }

    public static Message parseToObject(String response) {
        return new Gson().fromJson(response, Message.class);
    }
}
