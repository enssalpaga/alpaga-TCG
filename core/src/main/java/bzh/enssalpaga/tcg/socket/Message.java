package bzh.enssalpaga.tcg.socket;

public class Message {
    private String event;
    private String content;

    public Message(String event, String content){
        this.event = event;
        this.content = content;
    }

    public String getEvent() {
        return event;
    }

    public String getContent() {
        return content;
    }
}
