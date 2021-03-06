package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.ConversationServiceConnectivity;
import model.ServerCallback;

public class CreateGroupActivity extends AppCompatActivity {

    //Activity to allow admin users to create a new group which students/lecturers can join

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
        buttonEffect(createGroup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.create_group_heading);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        createGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ArrayList<String> allKeys = new ArrayList<>();
                if (createGroup.getText().toString().equalsIgnoreCase("Create Group")) {
                    if(!groupName.getText().toString().equalsIgnoreCase("") && !enrolmentKey.getText().toString().equalsIgnoreCase("")) {
                        convo.getExistingConversations(new ServerCallback() {

                            @Override
                            public void onSuccess(JSONObject result) {
                            }

                            @Override
                            public void onSuccess(JSONArray result) {
                                boolean keyExists = false;
                                for (int i = 0; i < result.length(); i++) {
                                    try {
                                        JSONObject res = (JSONObject) result.get(i);
                                        String key = res.getString("Key");
                                        allKeys.add(key);
                                    } catch (JSONException r) {
                                        System.out.println(r.getMessage());
                                    }
                                }

                                for (int i = 0; i < allKeys.size(); i++) {
                                    if (enrolmentKey.getText().toString().trim().equalsIgnoreCase(allKeys.get(i))) {
                                        keyExists = true;
                                    }
                                }
                                if (keyExists) {
                                    Toast.makeText(getApplicationContext(), "Key already exists. Please choose a different key", Toast.LENGTH_LONG).show();
                                    hidepDialog();
                                }
                                if (!keyExists) {
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
                                            createGroup.setText(R.string.Ok);
                                            textView.setText(R.string.conversation_key);
                                            textView.setPadding(0, 20, 0, 0);

                                            convo.AddUserIntoConversation(new ServerCallback() {
                                                @Override
                                                public void onSuccess(JSONObject result) {
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
                                            }, key, LogInActivity.loggedInUser, LogInActivity.password, String.valueOf(LogInActivity.loggedInUserType));
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
                                    }, key, gName, LogInActivity.loggedInUser);
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

                } else if (createGroup.getText().toString().equalsIgnoreCase("OK")) {
                    Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                    startActivity(t);
                    finish();

                }
            }
        });


    }

    //return to previous screen
    @Override
    public void onBackPressed()
    {
        Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
        startActivity(t);
        finish();
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

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
