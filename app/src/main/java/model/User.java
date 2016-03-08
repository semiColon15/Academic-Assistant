package model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String emailAddress;
    private String password;
    private String confirmPassword;
    boolean adminUser;
    private List<Conversation> conversations;

    public User(String email, String password, String confirmPassword)
    {
        this.emailAddress = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public User(String email, String password, String confirmPassword, boolean adminUser)
    {
        this.emailAddress = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.adminUser = adminUser;
    }

    public User(String email, String password, boolean adminUser, ArrayList<Conversation> convos)
    {
        this.emailAddress = email;
        this.password = password;
        this.adminUser = adminUser;
        this.conversations = convos;
    }

    public String getEmail() { return emailAddress; }

    public String getPassword()
    {
        return password;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public boolean getAdminUser() { return adminUser; }

    public List<Conversation> getConversations() { return conversations; }
}
