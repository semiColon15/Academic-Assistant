package com.hooper.kenneth.academicassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import model.User;

public class LogInActivity extends Activity {

    private TextView username;
    private TextView password;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button logInButton;
    private Button signUpButton;

    private static LogInActivity sInstance;
    private RequestQueue mRequestQueue;
    private String jsonResponse;
    private ProgressDialog pDialog;

    private static String TAG = LogInActivity.class.getSimpleName();
    final String baseUrl = "http://academicassistant2.azurewebsites.net/api/";

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
    }

    public void verifyLogIn()
    {
        User.verifyLogIn(usernameInput.getText().toString(), passwordInput.getText().toString());
    }
}
