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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import model.ConversationServiceConnectivity;
import model.ServerCallback;

public class JoinGroupActivity extends AppCompatActivity {

    //Activity to join a user into an existing group

    private ConversationServiceConnectivity convo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_lecturer);
        ProgressDialog pDialog = new ProgressDialog(this);

        convo = new ConversationServiceConnectivity(getApplicationContext(), pDialog);
        final EditText key = (EditText) findViewById(R.id.enrolKey1);
        Button ok = (Button) findViewById(R.id.joinGroup1);
        buttonEffect(ok);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                convo.AddUserIntoConversation(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        Intent t = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                        startActivity(t);
                        finish();
                    }

                    @Override
                    public void onSuccess(JSONArray result) {

                    }

                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Invalid Key", Toast.LENGTH_LONG).show();
                    }
                }, key.getText().toString().trim(), LogInActivity.loggedInUser, LogInActivity.password, String.valueOf(LogInActivity.loggedInUserType));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.join_group);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);
    }

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
}