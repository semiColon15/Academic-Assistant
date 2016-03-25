package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;

import model.ConversationServiceConnectivity;
import model.ServerCallback;

public class CreateGroupActivity extends AppCompatActivity {

    public static String groupKey;

    private Button createGroup;
    private ConversationServiceConnectivity convo;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_lecturer);
        pDialog = new ProgressDialog(this);

        convo = new ConversationServiceConnectivity(getApplicationContext(), pDialog);

        final TextView showKey = (TextView) findViewById(R.id.tv);
        final TextView textView = (TextView) findViewById(R.id.groupName);
        final EditText groupName = (EditText) findViewById(R.id.groupNameInput);
        final TextView keyName = (TextView) findViewById(R.id.enrolKeyText);
        final EditText enrolmentKey = (EditText) findViewById(R.id.enrolKey);
        createGroup = (Button) findViewById(R.id.createGroupButton);


        createGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (createGroup.getText().toString().equalsIgnoreCase("Create Group")) {
                    final String key = enrolmentKey.getText().toString().trim();
                    final String gName = groupName.getText().toString().trim();
                    convo.CreateConversation(new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Toast.makeText(getApplicationContext(), "Successfully created group", Toast.LENGTH_LONG).show();


                            ((ViewManager) createGroup.getParent()).removeView(groupName);
                            ((ViewManager) createGroup.getParent()).removeView(enrolmentKey);
                            ((ViewManager) createGroup.getParent()).removeView(keyName);
                            showKey.setText(key);
                            createGroup.setText("OK");
                            textView.setText("Conversation Key:");
                            textView.setPadding(0, 20, 0, 0);

                            groupKey = key;

                            convo.AddUserIntoConversation(new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {

                                    Toast.makeText(getApplicationContext(), "WORKED", Toast.LENGTH_LONG).show();
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
                            }, groupKey, LogInActivity.loggedInUser, LogInActivity.password, String.valueOf(LogInActivity.loggedInUserType));
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
                    }, key, gName, LogInActivity.loggedInUser);
                } else if (createGroup.getText().toString().equalsIgnoreCase("OK")) {
                    //DISPLAY NEXT SCREEN
                    Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                    startActivity(t);
                    finish();

                }
            }
        });
    }
}
