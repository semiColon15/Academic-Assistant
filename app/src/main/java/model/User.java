package model;

public abstract class User {

    private int userID;
    private String username;
    private String password;
    boolean adminUser;

    public User(int id, String username, String password, boolean adminUser)
    {
        this.userID = id;
        this.username = username;
        this.password = password;
        this.adminUser = adminUser;
    }

    public static boolean verifyLogIn(String username, String password)
    {
        //CONNECT TO WEB API HERE AND CHECK IF LOG IN IN VALID
        //http://academicassistant2.azurewebsites.net/api/User/GetUser/?username=Gar
        return false;
    }
}
