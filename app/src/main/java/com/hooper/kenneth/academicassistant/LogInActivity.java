package com.hooper.kenneth.academicassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

import model.ConversationServiceConnectivity;
import model.User;
import model.UserServiceConnectivity;

public class LogInActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button logInButton;
    private Button signUpButton;

    private String[] emails;
    private String[] passwords;

    public static String token;
    public static String password;

    static String loggedInUser;
    static boolean loggedInUserType;
    private boolean loggedIn;
    static ConversationServiceConnectivity c;

    private UserServiceConnectivity userServiceConnectivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        logInButton = (Button) findViewById(R.id.LogInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        c = new ConversationServiceConnectivity(getApplicationContext());
        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext());

        //Check if already logged in.
        token = retrieveToken();
        password = retrievePassword();
        loggedInUser = retrieveLoggedInUser();
        loggedInUserType = retrieveLoggedInUserType();
        System.out.println("LogInActivity::: Initial Log In: " + token);
        System.out.println("INITIAL TYPE: " + loggedInUserType);

        if (token.equals("") || token == null) {
            loggedIn = false;
        }
        else {
            loggedIn = true;
        }

        if (loggedIn)
        {
            if(loggedInUserType) {
                Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                startActivity(t);
                System.out.println("USER: " + loggedInUser);
                System.out.println("ADMIN: " + loggedInUserType);
                finish();
            }
            else
            {
                Intent t = new Intent(getApplicationContext(), ChooseConversationStudentActivity.class);
                startActivity(t);
                System.out.println("USER: " + loggedInUser);
                System.out.println("ADMIN: " + loggedInUserType);
                finish();
            }
        }


        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                emails = userServiceConnectivity.getAllEmailAddresses();
                passwords = userServiceConnectivity.getAllPasswords();
                userServiceConnectivity.checkUserAdminLevel(usernameInput.getText().toString());

                boolean g = false;
                if(passwords != null) {
                    for (int i = 0; i < passwords.length; i++) {
                        if (usernameInput.getText().toString().trim().equals(emails[i]) && passwordInput.getText().toString().equals(passwords[i])) {
                            g = true;
                            loggedInUser = usernameInput.getText().toString();
                        }
                    }
                    if (g) {
                        //TODO CHECK CURRENT TOKEN IS STILL VALID
                        Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                        startActivity(t);
                        User user = new User(usernameInput.getText().toString(), passwordInput.getText().toString(), passwordInput.getText().toString(), loggedInUserType);

                        userServiceConnectivity.getToken(user);

                        token = userServiceConnectivity.getToken();
                        password = passwordInput.getText().toString();
                        //loggedInUser = usernameInput.getText().toString();
                        loggedInUserType = userServiceConnectivity.getAdminLevel();

                        System.out.println("AAAAAAAAAAAAAAA " + token);
                        System.out.println("Logged IN USer: " + loggedInUser);
                        System.out.println("ADMIN TYPE IODFINDIND: " + loggedInUserType);
                        saveToken("token.txt", token, getApplicationContext());
                        savePassword("password.txt", password, getApplicationContext());
                        saveLoggedInUser("loggedInUser.txt", loggedInUser.trim(), getApplicationContext());
                        saveLoggedInUserType("loggedInUserType.txt", loggedInUserType, getApplicationContext());
                        System.out.println("SAVED USER: " + retrieveLoggedInUser());
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public String retrieveToken()
    {
        String token = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/token.txt"));
            String s = br.readLine();
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                token += p[i];
            }

            System.out.println("SOSN TOKEN: " + token);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static void saveToken(String filename, String token, Context ctx) {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(token);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public String retrievePassword()
    {
        String pword = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/password.txt"));
            String s = br.readLine();
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                pword += p[i];
            }

            System.out.println("SOSN TOKEN: " + pword);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pword;
    }

    public static void savePassword(String filename, String pword, Context ctx) {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pword);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public String retrieveLoggedInUser()
    {
        String user = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/loggedInUser.txt"));
            String s = br.readLine();
            System.out.println("IN GETTING METHOD::::::::: " + s);
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                user += p[i];
                System.out.println("TRANSFERRING::::::: " + user);
            }

            System.out.println("SOSN USER: " + user);
            System.out.println("ORIGINAL:::::: " + s);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;

    }

    public static void saveLoggedInUser(String filename, String user, Context ctx)
    {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean retrieveLoggedInUserType()
    {
        String type = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/loggedInUserType.txt"));
            String s = br.readLine();
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                type += p[i];
            }

            System.out.println("SOSN TYPE: " + type);
            System.out.println("SOSN TYPE 2: " + Boolean.valueOf(type));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.valueOf(type);
    }

    public static void saveLoggedInUserType(String filename, boolean type, Context ctx)
    {
        String type2 = String.valueOf(type);
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(type2);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static ConversationServiceConnectivity getConversationServiceConnectivity()
    {
        return c;
    }
}
