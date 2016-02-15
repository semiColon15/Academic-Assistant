package com.hooper.kenneth.academicassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.RequestFuture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.User;
import model.UserServiceConnectivity;

public class LogInActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button logInButton;
    private Button signUpButton;

    private static LogInActivity sInstance;
    private String[] emails;
    private String[] passwords;
    private boolean admin;

    public static String token;

    static String loggedInUser;
    static String loggedInUserType;
    private boolean loggedIn;

    private UserServiceConnectivity userServiceConnectivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        logInButton = (Button) findViewById(R.id.LogInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        sInstance = this;

        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext());

        //Check if already logged in.
        token = retrieveToken();
        System.out.println("LogInActivity::: Initial Log In: " + token);

        if (token.equals("") || token == null){
            loggedIn = false;
        }
        else {
            loggedIn = true;
        }

        if (loggedIn)
        {
            Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
            startActivity(t);
            loggedInUser = retrieveLoggedInUser();
            System.out.println("USER: " + loggedInUser);
            finish();
        }


        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                emails = userServiceConnectivity.getAllEmailAddresses();
                passwords = userServiceConnectivity.getAllPasswords();
                userServiceConnectivity.checkUserAdminLevel(usernameInput
                        .getText().toString());
                admin = userServiceConnectivity.getAdminLevel();

                boolean g = false;
                if(passwords != null) {
                    for (int i = 0; i < passwords.length; i++) {
                        if (usernameInput.getText().toString().trim().equals(emails[i]) && passwordInput.getText().toString().equals(passwords[i])) {
                            g = true;
                        }
                    }
                    if (g) {
                        //TODO CHECK CURRENT TOKEN IS STILL VALID
                        Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                        startActivity(t);
                        User user = new User(usernameInput.getText().toString(), passwordInput.getText().toString(), passwordInput.getText().toString(), admin);

                        userServiceConnectivity.getToken(user);

                        token = userServiceConnectivity.getToken();

                        System.out.println("AAAAAAAAAAAAAAA " + token);
                        saveToken("token.txt", token, getApplicationContext());
                        saveLoggedInUser("loggedInUser.txt", usernameInput.getText().toString(), getApplicationContext());
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

            System.out.println("SOSN: " + token);

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

    public String retrieveLoggedInUser()
    {
        String user = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/loggedInUser.txt"));
            String s = br.readLine();
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                user += p[i];
            }

            System.out.println("SOSN: " + user);

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
}
