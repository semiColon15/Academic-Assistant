package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    private Button ok;
    private ConversationServiceConnectivity convo;
    private ProgressDialog pDialog;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_lecturer);
        pDialog = new ProgressDialog(this);

        convo = new ConversationServiceConnectivity(getApplicationContext(), pDialog);
        final EditText key = (EditText) findViewById(R.id.enrolKey1);
        ok = (Button) findViewById(R.id.joinGroup1);
        buttonEffect(ok);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.chatify);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Join A Group");
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miInfo_add:
                //Intent i = new Intent(getApplicationContext(), JoinGroupActivity.class);
                //startActivity(i);
                //finish();
                return true;
            case R.id.miLogout_add:
                LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                LogInActivity.savePassword("password.txt", "", getApplicationContext());
                Intent e = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(e);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
}