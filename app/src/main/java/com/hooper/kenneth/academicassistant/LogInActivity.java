package com.hooper.kenneth.academicassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import model.User;
import model.UserServiceConnectivity;

public class LogInActivity extends Activity {

    private TextView username;
    private TextView password;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button logInButton;
    private Button signUpButton;

    private Boolean loggedIn;

    private static LogInActivity sInstance;
    private RequestQueue mRequestQueue;
    private String jsonResponse;
    private ProgressDialog pDialog;

    private String[] emails;
    private String[] passwords;

    private UserServiceConnectivity userServiceConnectivity;

    private static String TAG = LogInActivity.class.getSimpleName();
    final String baseUrl = "https://academicassistant20151209121006.azurewebsites.net/api/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        username = (TextView) findViewById(R.id.usernameTextView);
        password = (TextView) findViewById(R.id.passwordTextView);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        logInButton = (Button) findViewById(R.id.LogInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        sInstance = this;

        mRequestQueue = Volley.newRequestQueue(this);

        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext());

        emails = userServiceConnectivity.getAllEmailAddresses();
        passwords = userServiceConnectivity.getAllPasswords();

        //System.out.println(emails);

        //TODO CHECK IF the user is logged in. Redirect if already logged in.


        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean g = false;
                for(int i = 0; i < passwords.length; i++) {
                    if (usernameInput.getText().toString().equals(emails[i]) && passwordInput.getText().toString().equals(passwords[i])) {
                        g = true;
                    }
                }
                if(g)
                {
                    Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                    startActivity(t);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
