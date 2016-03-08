package com.hooper.kenneth.academicassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import model.ConversationServiceConnectivity;

public class CreateGroupActivity extends AppCompatActivity {

    public static String groupKey;

    Button createGroup;
    ConversationServiceConnectivity convo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_lecturer);

        convo = new ConversationServiceConnectivity(getApplicationContext());

        final TextView showKey = (TextView) findViewById(R.id.tv);
        final TextView textView = (TextView) findViewById(R.id.groupName);
        final EditText groupName = (EditText) findViewById(R.id.groupNameInput);
        createGroup = (Button) findViewById(R.id.createGroupButton);


        createGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //((ViewManager)createGroup.getParent()).removeView(createGroup);
                if (createGroup.getText().toString().equalsIgnoreCase("Create Group")) {
                    String key = generateKey();
                    String gName = groupName.getText().toString();
                    ((ViewManager) createGroup.getParent()).removeView(groupName);
                    showKey.setText(key);
                    createGroup.setText("OK");
                    textView.setText("Conversation Key:");
                    textView.setPadding(0, 20, 0, 0);

                    groupKey = key;
                    convo.CreateConversation(key, gName, LogInActivity.loggedInUser);
                } else if (createGroup.getText().toString().equalsIgnoreCase("OK")) {
                    //DISPLAY NEXT SCREEN
                    convo.AddUserIntoConversation(groupKey, LogInActivity.loggedInUser, LogInActivity.password , String.valueOf(LogInActivity.loggedInUserType));
                    Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                    startActivity(t);
                    finish();
                    Toast.makeText(getApplicationContext(), "WORKED", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String generateKey()
    {
        String[] allIds = convo.retrieveAllConversationKeys();
        String[] chars = new String[6];
        Random rand = new Random();
        String output = new String();
        boolean goAgain = true;

        while(goAgain) {
            for (int i = 0; i < chars.length; i++) {
                int randomNum = rand.nextInt(9 + 1);
                chars[i] = Integer.toString(randomNum);
            }
            for (int i = 0; i < chars.length; i++) {
                output += chars[i];
                goAgain = false;
            }
            for (int i = 0; i < allIds.length; i++) {
                if (allIds[i].equalsIgnoreCase(output)) {
                    goAgain = true;
                }
            }
        }
        return output;
    }
}
