package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import model.ConversationServiceConnectivity;
import model.ServerCallback;

public class JoinGroupActivity extends AppCompatActivity {

    private Button ok;
    private ConversationServiceConnectivity convo;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_lecturer);
        pDialog = new ProgressDialog(this);

        convo = new ConversationServiceConnectivity(getApplicationContext(), pDialog);
        final EditText key = (EditText) findViewById(R.id.enrolKey1);
        ok = (Button) findViewById(R.id.joinGroup1);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                convo.AddUserIntoConversation(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        Toast.makeText(getApplicationContext(), "WORKED", Toast.LENGTH_LONG).show();
                        if(LogInActivity.loggedInUserType == true) {
                            Intent t = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                            startActivity(t);
                            finish();
                        }
                        else {
                            Intent t = new Intent(getApplicationContext(), ChooseConversationStudentActivity.class);
                            startActivity(t);
                            finish();
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
                        Toast.makeText(getApplicationContext(), "Invalid Key", Toast.LENGTH_LONG).show();
                    }
                }, key.getText().toString().trim(), LogInActivity.loggedInUser, LogInActivity.password, String.valueOf(LogInActivity.loggedInUserType));
            }
        });

    }
}