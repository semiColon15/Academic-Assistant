package model;

import java.util.ArrayList;

public class Conversation {

    private String key;
    private String conversationName;
    private String administrator;
    private ArrayList<User> members;
    private ArrayList<Message> messages;

    public Conversation(String key, String name, String admin, ArrayList<User> members, ArrayList<Message> messages)
    {
        this.key = key;
        conversationName = name;
        administrator = admin;
        this.members = members;
        this.messages = messages;
    }

    public String getKey()
    {
        return key;
    }

    public String getConversationName()
    {
        return conversationName;
    }

    public String getAdministrator()
    {
        return administrator;
    }

    public ArrayList<User> getMembers()
    {
        return members;
    }
}
