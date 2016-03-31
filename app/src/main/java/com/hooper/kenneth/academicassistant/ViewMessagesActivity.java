package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import model.*;

public class ViewMessagesActivity extends AppCompatActivity {

    private EditText messageText;
    private MessageServiceConnectivity connectivity;
    private TableLayout tableLayout;
    private ScrollView scrollView;
    private String keyConvo;
    private String nameConvo;
    private String groupAdmin;
    private ArrayList<String> userList;
    private ConversationServiceConnectivity c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messageText = (EditText) findViewById(R.id.messageText);
        Button sendButton = (Button) findViewById(R.id.sendButton);

        buttonEffect(sendButton);

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        tableLayout = (TableLayout) findViewById(R.id.table);
        tableLayout.setVerticalScrollBarEnabled(true);

        if(LogInActivity.loggedInUserType) {
            keyConvo = ChooseConversationLecturerActivity.chosenConvoKey;
            nameConvo = ChooseConversationLecturerActivity.chosenGroupName;
        }
        else {
            keyConvo = ChooseConversationStudentActivity.chosenConvoKey;
            nameConvo = ChooseConversationStudentActivity.chosenGroupName;
        }

        c = new ConversationServiceConnectivity(getApplicationContext(), pDialog);



        scrollView = (ScrollView) findViewById(R.id.scroll);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.chatify);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(nameConvo);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                ViewGroup.LayoutParams params = tableLayout.getLayoutParams();
                ViewGroup.LayoutParams params2 = scrollView.getLayoutParams();
                if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                    params.height = 300;
                    params2.height = 300;
                    tableLayout.setLayoutParams(params);
                    scrollView.setLayoutParams(params2);
                }
                else if(heightDiff < 300)
                {
                    params.height = 600;
                    params2.height = 600;
                    tableLayout.setLayoutParams(params);
                    scrollView.setLayoutParams(params2);
                }
            }
        });

        connectivity = new MessageServiceConnectivity(getApplicationContext(), this, pDialog, tableLayout);

        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              if(!messageText.getText().toString().equalsIgnoreCase("")){

                                                  if(LogInActivity.loggedInUserType) {
                                                      connectivity.SendMessage(messageText.getText().toString(), keyConvo, LogInActivity.loggedInUser, keyConvo);
                                                  }
                                                  else
                                                  {
                                                      connectivity.SendMessage(messageText.getText().toString(), keyConvo, LogInActivity.loggedInUser, keyConvo);
                                                  }
                                                  final TableRow tableRow = new TableRow(getApplicationContext());
                                                  tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                                  tableRow.setBackgroundResource(R.drawable.bubble_green);

                                                  final TextView message = new TextView(getApplicationContext());
                                                  message.setText(messageText.getText().toString());
                                                  message.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                                  message.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);

                                                  final TextView sender = new TextView(getApplicationContext());
                                                  sender.setText(LogInActivity.loggedInUser + "\t");
                                                  sender.setPadding(0, 0, 10, 0);
                                                  sender.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.09f));
                                                  sender.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);

                                                  tableRow.addView(sender);
                                                  tableRow.addView(message);

                                                  tableLayout.addView(tableRow);
                                                  messageText.setText("");

                                                  if(LogInActivity.loggedInUserType) {
                                                      ChooseConversationLecturerActivity.saveKey("", getApplicationContext());
                                                  }
                                                  else
                                                  {
                                                      ChooseConversationStudentActivity.saveKey("", getApplicationContext());
                                                  }
                                              }
                                          }
                                      }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miInfo_msg:
                openPopUpWindow();
                return true;
            case R.id.miLogout_msg:
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

    public void openPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) ViewMessagesActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_message_info_popup,
                (ViewGroup) findViewById(R.id.glayout1));
        final PopupWindow pwindo = new PopupWindow(layout, 470, 850, true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        /*Toolbar toolbar2;
        toolbar2 = (Toolbar) findViewById(R.id.tool_info);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar2.findViewById(R.id.toolbar_title);
        mTitle.setText("Info");
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);*/

        c.getConversation(new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                userList = new ArrayList<>();
                try {
                    JSONArray users = result.getJSONArray("Users");
                    if (users != null) {
                        for (int j = 0; j < users.length(); j++) {

                            JSONObject users2 = (JSONObject) users.get(j);
                            String email = users2.getString("Email");
                            userList.add(email);
                        }
                    }
                    groupAdmin = result.getString("Administrator");

                    TableLayout tableLayout = (TableLayout) pwindo.getContentView().findViewById(R.id.convos2);

                    if (userList != null) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i) != null) {
                                final TableRow tableRow = new TableRow(getApplicationContext());
                                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                                final TextView member = new TextView(getApplicationContext());
                                member.setText(userList.get(i));
                                member.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                member.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);

                                tableRow.addView(member);
                                tableLayout.addView(tableRow);
                            }
                        }
                    }

                    TextView gName = (TextView) pwindo.getContentView().findViewById(R.id.nameDisplay_info);
                    gName.setText(nameConvo);
                    TextView gAdmin = (TextView) pwindo.getContentView().findViewById(R.id.adminDisplay_info);
                    gAdmin.setText(groupAdmin);
                    TextView key = (TextView) pwindo.getContentView().findViewById(R.id.keyDisplay_info);
                    if (LogInActivity.loggedInUser.equalsIgnoreCase(groupAdmin)) {
                        key.setText(keyConvo);
                    } else {
                        key.setText("Admin access needed.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSuccess(JSONArray result) {
            }

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError(VolleyError error) {
            }
        }, keyConvo);


        Button closePopup = (Button) pwindo.getContentView().findViewById(R.id.okButton_info);
        closePopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwindo.dismiss();
            }
        });
        buttonEffect(closePopup);
    }
}