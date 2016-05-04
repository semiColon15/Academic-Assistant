package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
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

    ProgressDialog pDialog;

    private UserServiceConnectivity userServiceConnectivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        Button logInButton = (Button) findViewById(R.id.LogInButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        buttonEffect(logInButton);
        buttonEffect(signUpButton);

        pDialog = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.log_in);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

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
            Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
            startActivity(t);
            finish();
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
                            hidepDialog();
                        }

                        boolean g = false;
                        if (passwords != null) {
                            for (int i = 0; i < passwords.length; i++) {
                                if (usernameInput.getText().toString().trim().equals(emails[i]) && passwordInput.getText().toString().equals(passwords[i])) {
                                    g = true;
                                    loggedInUser = usernameInput.getText().toString();
                                }
                            }
                            if (g) {

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

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Account Registration Error", Toast.LENGTH_LONG).show();
                                            hidepDialog();
                                        }

                                        password = passwordInput.getText().toString().trim();
                                        loggedInUser = usernameInput.getText().toString().trim();

                                        userServiceConnectivity.checkUserAdminLevel(new ServerCallback() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                try {
                                                    String adminLevel_ = result.getString("Admin");

                                                    saveToken("token.txt", token, getApplicationContext());
                                                    savePassword("password.txt", password, getApplicationContext());
                                                    saveLoggedInUser("loggedInUser.txt", loggedInUser.trim(), getApplicationContext());
                                                    saveLoggedInUserType("loggedInUserType.txt", Boolean.valueOf(adminLevel_), getApplicationContext());
                                                    loggedInUserType = Boolean.valueOf(adminLevel_);

                                                    Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                                                    startActivity(t);
                                                    finish();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                hidepDialog();
                                            }

                                            @Override
                                            public void onSuccess(JSONArray result) {

                                            }

                                            @Override
                                            public void onSuccess(String result) {

                                            }

                                            @Override
                                            public void onError(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                hidepDialog();
                                            }
                                        }, usernameInput.getText().toString());
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    }
                                }, user);

                            } else {
                                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                                hidepDialog();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onError(VolleyError error) {
                        hidepDialog();
                    }
                });
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        //return decrypt(pword, 3);
        return pword;
    }

    public static void savePassword(String filename, String pword, Context ctx) {
        FileOutputStream fos;
        //String p = encrypt(pword, 3);
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
//        return decrypt(user,4);
        return user;

    }

    public static void saveLoggedInUser(String filename, String user, Context ctx)
    {
        //String f = encrypt(user, 4);
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

   /* private static String encrypt(String token, int num) {
        String s = "";
        int len = token.length();
        for (int x = 0; x < len; x++) {
            char c = (char) (token.charAt(x) + num);
            if (c > 'z')
                s += (char) (token.charAt(x) - (26 - num));
            else
                s += (char) (token.charAt(x) + num);
        }
        return s;
    }

    private String decrypt(String token, int num)
    {
        String s = "";
        int len = token.length();
        for (int x = 0; x < len; x++) {
            char c = (char) (token.charAt(x) - num);
            if (c > 'z')
                s += (char) (token.charAt(x) + (26 - num));
            else
                s += (char) (token.charAt(x) - num);
        }
        return s;
    }*/

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
