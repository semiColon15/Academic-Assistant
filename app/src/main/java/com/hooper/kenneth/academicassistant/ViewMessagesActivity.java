package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import model.*;

public class ViewMessagesActivity extends AppCompatActivity {

    private EditText messageText;
    private MessageServiceConnectivity connectivity;
    private UserServiceConnectivity u;
    private TableLayout tableLayout;
    private ScrollView scrollView;
    private String keyConvo;
    private String nameConvo;
    private String groupAdmin = "";
    private ArrayList<String> allSenders;
    private ArrayList<String> allMessages;
    private ArrayList<Integer> allMessageIDs;
    private ArrayList<String> allTimeStamps;
    private ArrayList<String> userList;
    private ConversationServiceConnectivity c;
    private ArrayList<TableRow> highlightedRows;
    private ImageView deleteIcon;
    private int deletePos;
    private int tablePos;
    private int personDeleteIndex;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messageText = (EditText) findViewById(R.id.messageText);

        ImageView sendButton = (ImageView) findViewById(R.id.send_button);
        sendButton.setImageResource(R.drawable.ic_send_black_24dp);
        sendButton.setClickable(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        tableLayout = (TableLayout) findViewById(R.id.table);
        tableLayout.setVerticalScrollBarEnabled(true);

        keyConvo = ChooseConversationActivity.chosenConvoKey;
        nameConvo = ChooseConversationActivity.chosenGroupName;

        c = new ConversationServiceConnectivity(getApplicationContext(), pDialog);

        highlightedRows = new ArrayList<>();

        scrollView = (ScrollView) findViewById(R.id.scroll);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(nameConvo);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        u = new UserServiceConnectivity(getApplicationContext(), pDialog);

        connectivity = new MessageServiceConnectivity(getApplicationContext(), pDialog);
        getViewInfo();


        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {

                                              Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                                              Date currentLocalTime = cal.getTime();
                                              DateFormat date = SimpleDateFormat.getDateTimeInstance();

                                              date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

                                              String time = date.format(currentLocalTime);
                                              allTimeStamps.add(time);

                                              if (!messageText.getText().toString().equalsIgnoreCase("") && messageText.getText().toString().trim().length() > 0) {

                                                  connectivity.SendMessage(new ServerCallback() {
                                                      @Override
                                                      public void onSuccess(JSONObject result) {
                                                          scrollView.fullScroll(View.FOCUS_DOWN);
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
                                                  }, messageText.getText().toString(), keyConvo, LogInActivity.loggedInUser, time, keyConvo);

                                                  final TableRow senderRow = new TableRow(getApplicationContext());
                                                  senderRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                                                  final TextView senderInitial = new TextView(getApplicationContext());
                                                  senderInitial.setText(LogInActivity.loggedInUser);
                                                  senderInitial.setPadding(20, 0, 0, 0);
                                                  senderInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                                  senderInitial.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);
                                                  senderInitial.setTextColor(getResources().getColor(R.color.colorPrimary));


                                                  final TableRow messageRow = new TableRow(getApplicationContext());
                                                  messageRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                                                  final TextView messageInitial = new TextView(getApplicationContext());
                                                  messageInitial.setText(messageText.getText().toString());
                                                  messageInitial.setPadding(0, 0, 0, 10);
                                                  messageInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                                  messageInitial.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);


                                                  final TableRow dateTimeRow = new TableRow(getApplicationContext());
                                                  dateTimeRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                                                  final TextView timeStamp = new TextView(getApplicationContext());
                                                  timeStamp.setText(time);
                                                  timeStamp.setPadding(20, 0, 0, 0);
                                                  timeStamp.setTextAppearance(getApplicationContext(), R.style.AppTheme);
                                                  timeStamp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                                                  messageInitial.setBackgroundResource(R.drawable.chat_bubble_me);
                                                  messageInitial.setPadding(20, 20, 20, 20);
                                                  messageRow.setPadding(80, 10, 20, 10);
                                                  timeStamp.setGravity(Gravity.END);
                                                  timeStamp.setPadding(0, 0, 20, 0);
                                                  senderInitial.setGravity(Gravity.END);
                                                  senderInitial.setPadding(0, 0, 20, 0);


                                                  messageRow.setLongClickable(true);

                                                  dateTimeRow.addView(timeStamp);
                                                  senderRow.addView(senderInitial);
                                                  messageRow.addView(messageInitial);

                                                  tableLayout.addView(dateTimeRow);
                                                  tableLayout.addView(senderRow);
                                                  tableLayout.addView(messageRow);

                                                  messageRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                      @Override
                                                      public boolean onLongClick(View v) {
                                                          for (int g = 0; g < tableLayout.getChildCount(); g++) {
                                                              if (v == tableLayout.getChildAt(g)) {
                                                                  highlightRow(messageRow);
                                                                  deletePos = (g - 1) / 3;
                                                                  tablePos = (deletePos * 3) + 2;
                                                                  System.out.println("DELETE POS " + deletePos);
                                                                  System.out.println("TABLE POS " + tablePos);
                                                                  return true;
                                                              }
                                                          }
                                                          return true;
                                                      }
                                                  });

                                                  messageText.setText("");

                                                  ChooseConversationActivity.saveKey("", getApplicationContext());

                                                  scrollView.fullScroll(View.FOCUS_DOWN);

                                                  String key_ = ChooseConversationActivity.chosenConvoKey;

                                                  //Send Notification
                                                  connectivity.sendMessageNotification(new ServerCallback() {
                                                      @Override
                                                      public void onSuccess(JSONObject result) {
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
                                                  }, key_, "New Message in " + nameConvo,LogInActivity.loggedInUser);
                                              }
                                          }
                                      }
        );
    }

    public void getViewInfo()
    {
        connectivity.setInitialViews(new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }

            @Override
            public void onSuccess(JSONArray response) {
                try {
                    allSenders = new ArrayList<>();
                    allMessages = new ArrayList<>();
                    allMessageIDs = new ArrayList<>();
                    allTimeStamps = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject message = (JSONObject) response.get(i);
                        String key = message.getString("ConversationKey");
                        String type = ChooseConversationActivity.chosenConvoKey;

                        if (key.equalsIgnoreCase(type)) {
                            String sender = message.getString("Sender");
                            String message_ = message.getString("MessageContent");
                            int messageID = message.getInt("MessageID");
                            String timeStamp = message.getString("TimeStamp");
                            allSenders.add(sender);
                            allMessages.add(message_);
                            allMessageIDs.add(messageID);
                            allTimeStamps.add(timeStamp);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                c.getConversation(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            groupAdmin = result.getString("Administrator");

                            setInitialViews();
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });

                            hidepDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        hidepDialog();
                    }
                }, keyConvo);
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

    public void setViewsRefresh()
    {
        connectivity.setInitialViewsNoDialog(new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }

            @Override
            public void onSuccess(JSONArray response) {
                try {
                    allSenders = new ArrayList<>();
                    allMessages = new ArrayList<>();
                    allMessageIDs = new ArrayList<>();
                    allTimeStamps = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject message = (JSONObject) response.get(i);
                        String key = message.getString("ConversationKey");
                        String type = ChooseConversationActivity.chosenConvoKey;

                        if (key.equalsIgnoreCase(type)) {
                            String sender = message.getString("Sender");
                            String message_ = message.getString("MessageContent");
                            int messageID = message.getInt("MessageID");
                            String timeStamp = message.getString("TimeStamp");
                            allSenders.add(sender);
                            allMessages.add(message_);
                            allMessageIDs.add(messageID);
                            allTimeStamps.add(timeStamp);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                setInitialViews();
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    public void setInitialViews()
    {
        for (int i = 0; i < allMessages.size(); i++) {
            if(allSenders != null) {

                final TableRow senderRow = new TableRow(getApplicationContext());
                senderRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                final TextView senderInitial = new TextView(getApplicationContext());
                senderInitial.setText(allSenders.get(i));
                senderInitial.setPadding(20, 0, 0, 0);
                senderInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                senderInitial.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);
                senderInitial.setTextColor(getResources().getColor(R.color.colorPrimary));


                final TableRow messageRow = new TableRow(getApplicationContext());
                messageRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                final TextView messageInitial = new TextView(getApplicationContext());
                messageInitial.setText(allMessages.get(i));
                messageInitial.setPadding(0, 0, 0, 10);
                messageInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                messageInitial.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);


                final TableRow dateTimeRow = new TableRow(getApplicationContext());
                dateTimeRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                final TextView timeStamp = new TextView(getApplicationContext());
                timeStamp.setText(allTimeStamps.get(i));
                timeStamp.setPadding(20, 0, 0, 0);
                timeStamp.setTextAppearance(getApplicationContext(), R.style.AppTheme);
                timeStamp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                if (allSenders.get(i).equalsIgnoreCase(LogInActivity.loggedInUser)) {

                    messageInitial.setBackgroundResource(R.drawable.chat_bubble_me);
                    messageInitial.setPadding(20, 20, 20, 20);
                    messageRow.setPadding(80, 10, 20, 10);
                    timeStamp.setGravity(Gravity.END);
                    timeStamp.setPadding(0, 0, 20, 0);
                    senderInitial.setGravity(Gravity.END);
                    senderInitial.setPadding(0, 0, 20, 0);

                } else {
                    if (groupAdmin.equalsIgnoreCase(allSenders.get(i))) {
                        messageInitial.setBackgroundResource(R.drawable.chat_bubble_admin);
                    } else {
                        messageInitial.setBackgroundResource(R.drawable.chat_bubble_others);
                    }
                    messageInitial.setPadding(20, 20, 20, 20);
                    messageRow.setPadding(20, 10, 80, 10);
                    timeStamp.setPadding(20, 0, 0, 0);
                    senderInitial.setPadding(20, 0, 0, 0);
                }
                messageRow.setLongClickable(true);

                dateTimeRow.addView(timeStamp);
                senderRow.addView(senderInitial);
                messageRow.addView(messageInitial);

                tableLayout.addView(dateTimeRow);
                tableLayout.addView(senderRow);
                tableLayout.addView(messageRow);

                messageRow.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for (int g = 0; g < tableLayout.getChildCount(); g++) {
                            if (v == tableLayout.getChildAt(g)) {
                                highlightRow(messageRow);
                                deletePos = (g - 1) / 3;
                                tablePos = (deletePos * 3) + 2;
                                return true;
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }



    public void highlightRow(final TableRow row)
    {
        if(highlightedRows != null) {
            if(highlightedRows.size() < 1) {

                row.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.highlight));
                highlightedRows.add(row);

                deleteIcon = new ImageView(this);
                deleteIcon.setImageResource(R.drawable.ic_delete_black_24dp);
                deleteIcon.setPadding(20, 20, 20, 20);
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

                                //CHECKS FOR ADMIN DELETE ACCESS GO HERE
                                if(LogInActivity.loggedInUser.equalsIgnoreCase(groupAdmin)) {
                                    openPopUpDeleteWindow();
                                }
                                else {
                                    if(LogInActivity.loggedInUser.equalsIgnoreCase(((TextView)((TableRow) tableLayout.getChildAt(tablePos-1)).getChildAt(0)).getText().toString()))
                                    {
                                        openPopUpDeleteWindow();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "You do not have permission to delete this message.", Toast.LENGTH_LONG).show();
                                    }
                                }
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
                                hidepDialog();
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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final PopupWindow pwindo = new PopupWindow(layout, (width-(width/4)), (height-(height/2)), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        TextView text = (TextView) layout.findViewById(R.id.question_info);
        text.setText(R.string.popup_msg_confirm_dlt);

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
            View view = tableLayout.getChildAt(tablePos);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                if(tablePos > 0) { tablePos--; }
                if(((TextView)((TableRow) tableLayout.getChildAt(tablePos)).getChildAt(0)).getText().toString().equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    row.getChildAt(0).setBackgroundResource(R.drawable.chat_bubble_me);
                    row.getChildAt(0).setPadding(20, 20, 20, 20);
                }
                else
                {
                    if (groupAdmin.equalsIgnoreCase(((TextView)((TableRow) tableLayout.getChildAt(tablePos)).getChildAt(0)).getText().toString())) {
                        row.getChildAt(0).setBackgroundResource(R.drawable.chat_bubble_admin);
                    } else {
                        row.getChildAt(0).setBackgroundResource(R.drawable.chat_bubble_others);
                    }
                    row.getChildAt(0).setPadding(20, 20, 20, 20);
                }
                deleteIcon.setClickable(false);
                ((ViewManager) row.getParent()).removeView(deleteIcon);
                row.removeView(deleteIcon);
            }
            highlightedRows.clear();
        }
        else if(numPressed == 1)
        {
            finish();
        }
        else if(numPressed == 2)
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miInfo_msg:
                openPopUpWindow();
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final PopupWindow pwindo = new PopupWindow(layout, width-(width/4), height-(height/4), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

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

                    final TableLayout tableLayout = (TableLayout) pwindo.getContentView().findViewById(R.id.convos2);

                    if (userList != null) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i) != null) {
                                final TableRow tableRow = new TableRow(getApplicationContext());
                                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                                final TextView member = new TextView(getApplicationContext());
                                member.setText(userList.get(i));
                                member.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                member.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);

                                ImageView removeFromGroupIcon = new ImageView(getBaseContext());
                                removeFromGroupIcon.setImageResource(R.drawable.ic_delete_black_24dp);
                                removeFromGroupIcon.setPadding(20, 0, 0, 0);
                                removeFromGroupIcon.setClickable(true);


                                tableRow.addView(member);
                                if(groupAdmin.equalsIgnoreCase(LogInActivity.loggedInUser))
                                {
                                    tableRow.addView(removeFromGroupIcon);
                                }
                                tableLayout.addView(tableRow);

                                final int f = i;
                                removeFromGroupIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openConfirmLeaveGroupPopUp(tableLayout, pwindo);
                                        personDeleteIndex = f;
                                    }
                                });
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
                        key.setText(R.string.popup_msg_admin_needed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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
                hidepDialog();
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

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            tableLayout.removeAllViews();
            setViewsRefresh();
        }
    };

    public void openConfirmLeaveGroupPopUp(TableLayout d, final PopupWindow pw)
    {
        LayoutInflater inflater = (LayoutInflater) ViewMessagesActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_conversation_delete_popup,
                (ViewGroup) findViewById(R.id.glayout2));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final PopupWindow pwindo = new PopupWindow(layout, width-(width/4), height-(height/4), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        final String userNameToRemove = ((TextView) ((TableRow) d.getChildAt(personDeleteIndex)).getChildAt(0)).getText().toString();

        TextView text2 = (TextView) layout.findViewById(R.id.question_info);
        text2.setText(String.format(getResources().getString(R.string.remove_user), userNameToRemove));

        Button confirmLeave = (Button) layout.findViewById(R.id.delete_info);
        confirmLeave.setText(R.string.leave_group);
        confirmLeave.setOnClickListener(new View.OnClickListener() {

            User user;

            public void onClick(View v) {

                u.checkUserAdminLevel(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            String email = result.getString("Email");
                            String password = result.getString("Password");
                            Boolean adminLevel_ = result.getBoolean("Admin");
                            ArrayList<Conversation> convos = new ArrayList<>();

                            user = new User(email, password, adminLevel_, convos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (user != null) {
                            c.RemoveUserFromGroup(new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    Toast.makeText(getApplicationContext(), "User removed", Toast.LENGTH_LONG).show();
                                    pw.dismiss();
                                    hidepDialog();
                                    openPopUpWindow();
                                }

                                @Override
                                public void onSuccess(JSONArray result) {
                                }

                                @Override
                                public void onSuccess(String result) {
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    hidepDialog();
                                    Toast.makeText(getApplicationContext(), "Error, unable to leave group...", Toast.LENGTH_LONG).show();
                                }
                            }, keyConvo, user);
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
                        hidepDialog();
                    }
                }, userNameToRemove);

                pwindo.dismiss();
            }
        });

        Button cancelLeave = (Button) layout.findViewById(R.id.cancel_info);
        cancelLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwindo.dismiss();
            }
        });
        buttonEffect(cancelLeave);
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}