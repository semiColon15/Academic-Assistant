package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.ServerCallback;
import model.User;
import model.UserServiceConnectivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailAddress;
    private EditText password;
    private EditText confirmPassword;
    private boolean isAdminLevel;

    private UserServiceConnectivity userServiceConnectivity;

    private String[] emailAddresses;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        emailAddress = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        confirmPassword = (EditText) findViewById(R.id.passwordInput2);
        Button signUp = (Button) findViewById(R.id.createAccountButton);
        buttonEffect(signUp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.chatify);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.sign_up);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        ProgressDialog pDialog = new ProgressDialog(this);

        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext(), pDialog);


        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                userServiceConnectivity.retrieveAllUsers(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                    }

                    @Override
                    public void onSuccess(JSONArray response) {
                        try {

                            emailAddresses = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject emailsj = (JSONObject) response.get(i);

                                emailAddresses[i] = emailsj.getString("Email");
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

                if(performLocalChecks()) {
                    final User user = new User(emailAddress.getText().toString().trim(), password.getText().toString().trim(), confirmPassword.getText().toString().trim(), isAdminLevel);

                    //TODO CHECK USER IS REGISTERED BEFORE PROCEEDING    I.E. THAT INTERNET CONNECTION EXISTS

                    final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) {

                        userServiceConnectivity.registerUserWithService(new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                userServiceConnectivity.registerUser(new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {

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
                                                    LogInActivity.token = jsonResponse.getString("access_token");

                                                    LogInActivity.saveToken("token.txt", LogInActivity.token, getApplicationContext());

                                                    VolleyLog.v("Response:%n %s", response);
                                                    Toast.makeText(getApplicationContext(), "Account Registered. Token Recieved", Toast.LENGTH_LONG).show();

                                                    if(isAdminLevel) {
                                                        Intent i = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else
                                                    {
                                                        Intent i = new Intent(getApplicationContext(), ChooseConversationStudentActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Account Registration Error", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onError(VolleyError error)
                                            {
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                            }
                                        }, user);
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
                                }, user);
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
                        }, user);


                    } else {
                        // notify user you are not online
                        Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_stu:
                if (checked)
                    isAdminLevel = false;
                    break;
            case R.id.radio_lect:
                if (checked)
                    isAdminLevel = true;
                    break;
        }
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

    public boolean performLocalChecks()
    {
        boolean isValid = true;
        boolean continu = true;

        //CHECK THAT FIELDS ARE NOT BLANK
        if(emailAddress.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(getApplicationContext(), "You must enter an address", Toast.LENGTH_LONG).show();
            return false;
        }

        //CHECK TO SEE IF EMAIL IS ALREADY IN USE
        if(emailAddresses != null) {
            for (int i = 0; i < emailAddresses.length; i++) {
                if (emailAddress.getText().toString().equalsIgnoreCase(emailAddresses[i])) {
                    emailAddress.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Email address is in use", Toast.LENGTH_LONG).show();
                    continu = false;
                    isValid = false;
                }
            }
        }
        if(continu)
        {
            if(!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString()))
            {
                isValid = false;
                confirmPassword.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();

            }
            else if(!passwordMeetsRequirements())
            {
                isValid = false;
                password.setTextColor(Color.RED);
                confirmPassword.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Password does not meet requirements." +
                        "Must be 6 long and contain 1 uppercase, 1 digit and 1 non-digit/letter.", Toast.LENGTH_LONG).show();
            }
        }
        return isValid;
    }

    public boolean passwordMeetsRequirements() {
        boolean containsUpper = false;
        boolean containsNum = false;
        boolean containsNonLetterOrDigit = false;

        char[] passwordChar = password.getText().toString().trim().toCharArray();

        if (passwordChar.length < 6)
        {
            return false;
        }

        for (int i = 0; i < passwordChar.length; i++) {
            if (Character.isUpperCase(passwordChar[i])) {
                containsUpper = true;
            }
            if (Character.isDigit(passwordChar[i])) {
                containsNum = true;
            }
            if (!Character.isLetterOrDigit(passwordChar[i])) {
                containsNonLetterOrDigit = true;
            }
        }
        return (containsNonLetterOrDigit && containsNum && containsUpper);
    }
}
