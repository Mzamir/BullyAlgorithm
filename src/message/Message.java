package message;

import com.google.gson.Gson;

public class Message {
    private int id;
    private String messageTyp;
    private String content;

    public Message() {
    }

    public Message(int id, String messageTyp, String content) {
        this.id = id;
        this.messageTyp = messageTyp;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageTyp() {
        return messageTyp;
    }

    public void setMessageTyp(String messageTyp) {
        this.messageTyp = messageTyp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
