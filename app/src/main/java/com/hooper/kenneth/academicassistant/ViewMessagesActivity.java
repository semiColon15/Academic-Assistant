package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    /*private String[] allSenders;
    private String[] allMessages;*/
    private ArrayList<String> allSenders;
    private ArrayList<String> allMessages;
    private ArrayList<Integer> allMessageIDs;
    private ArrayList<String> userList;
    private ConversationServiceConnectivity c;
    private ArrayList<TableRow> highlightedRows;

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

        highlightedRows = new ArrayList<>();

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

        connectivity = new MessageServiceConnectivity(getApplicationContext(), pDialog);
        connectivity.setInitialViews(new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }

            @Override
            public void onSuccess(JSONArray response) {
                try {
                    /*allSenders = new String[response.length()];
                    allMessages = new String[response.length()];
                    allMessageIDs = new int[response.length()];*/
                    allSenders = new ArrayList<>();
                    allMessages = new ArrayList<>();
                    allMessageIDs = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject message = (JSONObject) response.get(i);
                        String key = message.getString("ConversationKey");
                        String type;
                        if(LogInActivity.loggedInUserType)
                        {
                            type = ChooseConversationLecturerActivity.chosenConvoKey;
                        }
                        else
                        {
                            type = ChooseConversationStudentActivity.chosenConvoKey;
                        }
                        if (key.equalsIgnoreCase(type))
                        {
                            String sender = message.getString("Sender");
                            String message_ = message.getString("MessageContent");
                            int messageID = message.getInt("MessageID");
                            allSenders.add(sender);
                            allMessages.add(message_);
                            allMessageIDs.add(messageID);
                        }
                    }
                    setInitialViews();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              if(!messageText.getText().toString().equalsIgnoreCase("")){

                                                  if(LogInActivity.loggedInUserType) {
                                                      connectivity.SendMessage(new ServerCallback() {
                                                          @Override
                                                          public void onSuccess(JSONObject result) {
                                                              connectivity.setViews(new ServerCallback() {
                                                                  @Override
                                                                  public void onSuccess(JSONObject result) {

                                                                  }

                                                                  @Override
                                                                  public void onSuccess(JSONArray result) {
                                                                      try {
                                                                          String jsonResponse = "";
                                                                          String senderText = "";
                                                                          String contentText = "";
                                                                          for (int i = 0; i < result.length(); i++) {

                                                                              JSONObject message = (JSONObject) result.get(i);

                                                                              String content = message.getString("messageContent");
                                                                              String sender = message.getString("sender");

                                                                              senderText += sender + ":\n";
                                                                              contentText += content + "\n";
                                                                          }

                                                                      } catch (JSONException e) {
                                                                          e.printStackTrace();
                                                                      }
                                                                  }

                                                                  @Override
                                                                  public void onSuccess(String result) {

                                                                  }

                                                                  @Override
                                                                  public void onError(VolleyError error) {

                                                                  }
                                                              });
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
                                                      }, messageText.getText().toString(), keyConvo, LogInActivity.loggedInUser, keyConvo);
                                                  }
                                                  /*else
                                                  {
                                                      connectivity.SendMessage(new ServerCallback() {
                                                          @Override
                                                          public void onSuccess(JSONObject result) {

                                                          }

                                                          @Override
                                                          public void onSuccess(JSONArray result) {
                                                              connectivity.setViews(new ServerCallback() {
                                                                  @Override
                                                                  public void onSuccess(JSONObject result) {

                                                                  }

                                                                  @Override
                                                                  public void onSuccess(JSONArray result) {
                                                                      try {
                                                                          String jsonResponse = "";
                                                                          String senderText = "";
                                                                          String contentText = "";
                                                                          for (int i = 0; i < result.length(); i++) {

                                                                              JSONObject message = (JSONObject) result.get(i);


                                                                              String content = message.getString("messageContent");
                                                                              String sender = message.getString("sender");

                                                                              senderText += sender + ":\n";
                                                                              contentText += content + "\n";
                                                                          }

                                                                      } catch (JSONException e) {
                                                                          e.printStackTrace();
                                                                      }
                                                                  }

                                                                  @Override
                                                                  public void onSuccess(String result) {

                                                                  }

                                                                  @Override
                                                                  public void onError(VolleyError error) {

                                                                  }
                                                              });
                                                          }*

                                                          @Override
                                                          public void onSuccess(String result) {

                                                          }

                                                          @Override
                                                          public void onError(VolleyError error) {

                                                          }
                                                      }, messageText.getText().toString(), keyConvo, LogInActivity.loggedInUser, keyConvo);
                                                  }*/
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
                                                  sender.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                                  sender.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);

                                                  tableRow.setLongClickable(true);
                                                  tableRow.setGravity(Gravity.CENTER);

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

                                                  tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                      @Override
                                                      public boolean onLongClick(View v) {
                                                          highlightRow(tableRow);
                                                          return true;
                                                      }
                                                  });
                                              }
                                          }
                                      }
        );
    }

    public void setInitialViews()
    {
        for (int i = 0; i < allMessages.size(); i++) {
            if(allSenders != null){

                final TableRow tableRow = new TableRow(getApplicationContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                if(allSenders.get(i).equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    tableRow.setBackgroundResource(R.drawable.bubble_green);
                }
                else
                {
                    tableRow.setBackgroundResource(R.drawable.bubble_yellow);
                }

                final TextView senderInitial = new TextView(getApplicationContext());
                senderInitial.setText(allSenders.get(i));
                senderInitial.setPadding(0, 0, 10, 0);
                senderInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                senderInitial.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);

                final TextView messageInitial = new TextView(getApplicationContext());
                messageInitial.setText(allMessages.get(i));
                messageInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                messageInitial.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);

                tableRow.setLongClickable(true);
                tableRow.setGravity(Gravity.CENTER);

                tableRow.addView(senderInitial);
                tableRow.addView(messageInitial);

                tableLayout.addView(tableRow);

                tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for(int g =0; g < allMessages.size();g++) {
                            if(v == tableLayout.getChildAt(g)) {
                                highlightRow(tableRow);
                                deletePos = g;
                                System.out.println("DELTE POS =  " + deletePos);
                                return true;
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    ImageView deleteIcon;
    int deletePos;
    public void highlightRow(final TableRow row)
    {
        if(highlightedRows != null) {
            if(highlightedRows.size() < 1) {

                row.setBackgroundColor(getResources().getColor(R.color.highlight));
                highlightedRows.add(row);

                deleteIcon = new ImageView(this);
                deleteIcon.setImageResource(R.drawable.ic_delete_black_24dp);
                deleteIcon.setPadding(20, 0, 0, 0);
                deleteIcon.setClickable(true);
                row.addView(deleteIcon);

                wasHighlighted = true;

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        c.getConversation(new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                userList = new ArrayList<>();
                                try {
                                    groupAdmin = result.getString("Administrator");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                System.out.print("ADMIN" + groupAdmin + "END");
                                System.out.print("LOGGED IN" + LogInActivity.loggedInUser + "END");

                                //CHECKS FOR ADMIN DELETE ACCESS GO HERE
                                if(LogInActivity.loggedInUser.equalsIgnoreCase(groupAdmin)) {
                                    openPopUpDeleteWindow();
                                }
                                else {
                                    if(LogInActivity.loggedInUser.equalsIgnoreCase(((TextView)((TableRow) tableLayout.getChildAt(deletePos)).getChildAt(0)).getText().toString())) // == user on that row
                                    {
                                        openPopUpDeleteWindow();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "You do not have permission to delete this message.", Toast.LENGTH_LONG).show();
                                    }
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
                    }
                });

                numPressed = 0;
            }
        }
    }

    public void openPopUpDeleteWindow()
    {
        LayoutInflater inflater = (LayoutInflater) ViewMessagesActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_conversation_delete_popup,
                (ViewGroup) findViewById(R.id.glayout2));
        final PopupWindow pwindo = new PopupWindow(layout, 470, 450, true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        TextView text = (TextView) layout.findViewById(R.id.question_info);
        text.setText("Are you sure you want to delete this message?");

        final Button confirmDelete = (Button) layout.findViewById(R.id.delete_info);
        buttonEffect(confirmDelete);
        confirmDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                connectivity.deleteMessage(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                    }

                    @Override
                    public void onSuccess(JSONArray result) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getApplicationContext(), "Message Deleted", Toast.LENGTH_LONG).show();
                        Intent f = new Intent(getApplicationContext(), ViewMessagesActivity.class);
                        startActivity(f);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                }, allMessageIDs.get(deletePos));


                pwindo.dismiss();
            }
        });

        Button cancelDelete = (Button) layout.findViewById(R.id.cancel_info);
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwindo.dismiss();
            }
        });
        buttonEffect(cancelDelete);
    }

    int numPressed = 0;
    boolean wasHighlighted = false;

    @Override
    public void onBackPressed()
    {
        numPressed++;
        if(numPressed == 1 && wasHighlighted) {
            View view = tableLayout.getChildAt(deletePos);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if(((TextView)((TableRow) tableLayout.getChildAt(deletePos)).getChildAt(0)).getText().toString().equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    row.setBackgroundResource(R.drawable.bubble_green);
                }
                else
                {
                    row.setBackgroundResource(R.drawable.bubble_yellow);
                }
                deleteIcon.setClickable(false);
                ((ViewManager) row.getParent()).removeView(deleteIcon);
                //row.setPadding(20, 20, 20, 20);
                row.setGravity(Gravity.CENTER);
                row.removeView(deleteIcon);
            }
            highlightedRows.clear();
        }
        else if(numPressed == 1 && !wasHighlighted)
        {
            finish();
        }
        else if(numPressed == 2)
        {
            finish();
        }
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