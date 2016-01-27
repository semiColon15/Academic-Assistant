package model;

public class User {

    private String emailAddress;
    private String password;
    private String confirmPassword;
    boolean adminUser;

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

    public String getEmail()
    {
        return emailAddress;
    }

    public String getPassword()
    {
        return password;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }
}
