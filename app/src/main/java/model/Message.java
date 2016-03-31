package model;

public class Message {

    public int id;
    public String message;
    public String recipient;
    public String sender;
    public String conversationKey;

    public Message(int id, String message, String recipient, String sender, String convo)
    {
        this.id = id;
        this.message = message;
        this.recipient = recipient;
        this.sender = sender;
        this.conversationKey = convo;
    }
}
