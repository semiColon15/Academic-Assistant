package model;

public class Message {

    public int id;
    public String message;
    public String recipient;
    public String sender;
    public String timeStamp;
    public String conversationKey;

    public Message(int id, String message, String recipient, String sender, String timeStamp, String convo)
    {
        this.id = id;
        this.message = message;
        this.recipient = recipient;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.conversationKey = convo;
    }
}
