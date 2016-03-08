package com.hooper.kenneth.academicassistant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import model.User;
import model.UserServiceConnectivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailAddress;
    private EditText password;
    private EditText confirmPassword;
    private Switch admin;
    private Button signUp;

    private UserServiceConnectivity userServiceConnectivity;

    private String[] emailAddresses;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        emailAddress = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        confirmPassword = (EditText) findViewById(R.id.passwordInput2);
        admin = (Switch) findViewById(R.id.switch1);
        signUp = (Button) findViewById(R.id.createAccountButton);

        userServiceConnectivity = new UserServiceConnectivity(getApplicationContext());

        admin.setChecked(false);
        admin.setText("Student");

        admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    admin.setText("Lecturer");
                } else {
                    admin.setText("Student");
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emailAddresses = userServiceConnectivity.getAllEmailAddresses();
                if(performLocalChecks()) {
                    boolean isAdminLevel;
                    if(admin.isChecked()) {
                        isAdminLevel = true;
                    }
                    else {
                        isAdminLevel = false;
                    }
                    User user = new User(emailAddress.getText().toString(), password.getText().toString(), confirmPassword.getText().toString(), isAdminLevel);

                    //TODO CHECK USER IS REGISTERED BEFORE PROCEEDING    I.E. THAT INTERNET CONNECTION EXISTS

                    final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) {
                        userServiceConnectivity.registerUserWithService(user);
                        Intent i = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Passed Checks", Toast.LENGTH_LONG).show();
                        finish();
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
            System.out.println("HERE 1");
        }
        if(continu)
        {
            if(!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString()))
            {
                isValid = false;
                confirmPassword.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                System.out.println("HERE 3");

            }
            else if(!passwordMeetsRequirements())
            {
                isValid = false;
                password.setTextColor(Color.RED);
                confirmPassword.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Password does not meet requirements." +
                        "Must be 6 long and contain 1 uppercase, 1 digit and 1 non-digit/letter.", Toast.LENGTH_LONG).show();
                System.out.println("HERE 4");
            }
        }
        return isValid;
    }

    public boolean passwordMeetsRequirements() {
        boolean containsUpper = false;
        boolean containsNum = false;
        boolean containsNonLetterOrDigit = false;

        char[] passwordChar = password.getText().toString().toCharArray();

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
