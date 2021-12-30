package message;

public class Message {
    private long timeStamp;
    private int processId;
    private String messageTyp;
    private String content;

    public Message() {
    }

    public Message(long timeStamp, int processId, String messageTyp, String content) {
        this.timeStamp = timeStamp;
        this.processId = processId;
        this.messageTyp = messageTyp;
        this.content = content;
    }

    public Message(int processId, String messageTyp, String content) {
        this.processId = processId;
        this.messageTyp = messageTyp;
        this.content = content;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
