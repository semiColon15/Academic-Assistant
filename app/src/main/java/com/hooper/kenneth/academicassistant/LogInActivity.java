package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import model.ServerCallback;
import model.User;
import model.UserServiceConnectivity;

public class LogInActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;

    private String[] emails;
    private String[] passwords;

    public static String token;
    public static String password;

    public static String loggedInUser;
    public static boolean loggedInUserType;

    private Toolbar toolbar;

    private UserServiceConnectivity userServiceConnectivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        Button logInButton = (Button) findViewById(R.id.LogInButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        ProgressDialog pDialog = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView c = getActionBarTextView();
        System.out.println("^^^^^^^^^^ " + c.getText().toString());

        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext(), pDialog);

        //Check if already logged in.
        token = retrieveToken();
        password = retrievePassword();
        loggedInUser = retrieveLoggedInUser();
        loggedInUserType = retrieveLoggedInUserType();

        boolean loggedIn;

        if (token.equals("")) {
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
                finish();
            }
            else
            {
                Intent t = new Intent(getApplicationContext(), ChooseConversationStudentActivity.class);
                startActivity(t);
                finish();
            }
        }


        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                userServiceConnectivity.retrieveAllUsers(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                    }

                    @Override
                    public void onSuccess(JSONArray response) {
                        try {

                            emails = new String[response.length()];
                            passwords = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject emailsj = (JSONObject) response.get(i);

                                emails[i] = emailsj.getString("Email");
                                passwords[i] = emailsj.getString("Password");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onError(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });



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

                        User user = new User(usernameInput.getText().toString(), passwordInput.getText().toString(), passwordInput.getText().toString(), loggedInUserType);

                        userServiceConnectivity.getToken(new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {

                            }

                            @Override
                            public void onSuccess(JSONArray result) {

                            }

                            @Override
                            public void onSuccess(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    token = jsonResponse.getString("access_token");

                                    LogInActivity.saveToken("token.txt", token, getApplicationContext());

                                    VolleyLog.v("Response:%n %s", response);
                                    Toast.makeText(getApplicationContext(), "Token Recieved", Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Account Registration Error", Toast.LENGTH_LONG).show();
                                }

                                password = passwordInput.getText().toString();
                                loggedInUser = usernameInput.getText().toString();

                                userServiceConnectivity.checkUserAdminLevel(new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        try {
                                            String adminLevel_ = result.getString("Admin");

                                            saveToken("token.txt", token, getApplicationContext());
                                            savePassword("password.txt", password, getApplicationContext());
                                            saveLoggedInUser("loggedInUser.txt", loggedInUser.trim(), getApplicationContext());
                                            saveLoggedInUserType("loggedInUserType.txt", Boolean.valueOf(adminLevel_), getApplicationContext());

                                            if(Boolean.valueOf(adminLevel_)) {
                                                Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                                                startActivity(t);
                                                finish();
                                            }
                                            else
                                            {
                                                Intent t = new Intent(getApplicationContext(), ChooseConversationStudentActivity.class);
                                                startActivity(t);
                                                finish();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onSuccess(JSONArray result) {

                                    }

                                    @Override
                                    public void onSuccess(String result) {

                                    }

                                    @Override
                                    public void onError(VolleyError error)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    }
                                }, usernameInput.getText().toString());
                            }

                            @Override
                            public void onError(VolleyError error)
                            {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }, user);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
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
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
                user += p[i];
            }


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
}
