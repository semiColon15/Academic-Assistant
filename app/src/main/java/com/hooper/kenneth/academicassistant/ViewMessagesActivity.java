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
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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

   /* String apiKey = "AIzaSyBbsP89ibvkKUEr8F6fOFKoO3fCZXZOfD8";
    String senderID = "113571816922";
    String DefaultFullAccessSignatrue = "Endpoint=sb://academicassistantbus.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Dp2/BtK9foRgfapH+1dpmMwlHtA6KFvQg6ieMEixXpM=";*/

    private EditText messageText;
    private MessageServiceConnectivity connectivity;
    private UserServiceConnectivity u;
    private TableLayout tableLayout;
    private ScrollView scrollView;
    private String keyConvo;
    private String nameConvo;
    private String groupAdmin;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        messageText = (EditText) findViewById(R.id.messageText);
        try {
            messageText.setText(this.getIntent().getStringExtra("text"));
        } catch(NullPointerException e)
        {
            Toast.makeText(getApplicationContext(), "IN CATCH", Toast.LENGTH_SHORT);
        }


        ImageView sendButton = (ImageView) findViewById(R.id.send_button);
        sendButton.setImageResource(R.drawable.ic_send_black_24dp);
        sendButton.setClickable(true);

        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        tableLayout = (TableLayout) findViewById(R.id.table);
        tableLayout.setVerticalScrollBarEnabled(true);

        if (LogInActivity.loggedInUserType) {
            keyConvo = ChooseConversationLecturerActivity.chosenConvoKey;
            nameConvo = ChooseConversationLecturerActivity.chosenGroupName;
        } else {
            keyConvo = ChooseConversationStudentActivity.chosenConvoKey;
            nameConvo = ChooseConversationStudentActivity.chosenGroupName;
        }

        c = new ConversationServiceConnectivity(getApplicationContext(), pDialog);

        highlightedRows = new ArrayList<>();

        scrollView = (ScrollView) findViewById(R.id.scroll);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.chatify);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(nameConvo);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int height = size.y;*/

        /*final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                ViewGroup.LayoutParams params = tableLayout.getLayoutParams();
                ViewGroup.LayoutParams params2 = scrollView.getLayoutParams();
                if (heightDiff > 100) {
                    params.height = 300;
                    params2.height = 300;
                    tableLayout.setLayoutParams(params);
                    scrollView.setLayoutParams(params2);
                }
                else if(heightDiff < 100)
                {
                    params.height = (height-(height/8));
                    params2.height = (height-(height/8));
                    tableLayout.setLayoutParams(params);
                    scrollView.setLayoutParams(params2);
                }
            }
        });*/

        u = new UserServiceConnectivity(getApplicationContext(), pDialog);

        connectivity = new MessageServiceConnectivity(getApplicationContext(), pDialog);
        setViews();


        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {

                                              Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                                              Date currentLocalTime = cal.getTime();
                                              DateFormat date = SimpleDateFormat.getDateTimeInstance();

                                              date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

                                              String time = date.format(currentLocalTime);
                                              allTimeStamps.add(time);
                                              System.out.println("TIME = " + time);

                                              if (!messageText.getText().toString().equalsIgnoreCase("")) {

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

                                                  final TableRow tableRow = new TableRow(getApplicationContext());
                                                  tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                                  tableRow.setBackgroundResource(R.drawable.bubble_green);

                                                  final TextView message = new TextView(getApplicationContext());
                                                  message.setText(messageText.getText().toString());
                                                  message.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                                  if (Build.VERSION.SDK_INT < 23) {
                                                      message.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);
                                                  } else {
                                                      message.setTextAppearance(R.style.messageTextStyle);
                                                  }

                                                  final TextView sender = new TextView(getApplicationContext());
                                                  sender.setText(LogInActivity.loggedInUser);
                                                  sender.setPadding(0, 0, 10, 0);
                                                  sender.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                                                  if (Build.VERSION.SDK_INT < 23) {
                                                      sender.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);
                                                  } else {
                                                      sender.setTextAppearance(R.style.senderTextStyle);
                                                  }


                                                  tableRow.setLongClickable(true);
                                                  tableRow.setGravity(Gravity.CENTER);

                                                  tableRow.addView(sender);
                                                  tableRow.addView(message);

                                                  final TableRow dateTimeRow = new TableRow(getApplicationContext());
                                                  tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                                                  final TextView timeStamp = new TextView(getApplicationContext());
                                                  timeStamp.setText(time);
                                                  timeStamp.setPadding(20, 0, 0, 0);
                                                  if (Build.VERSION.SDK_INT < 23) {
                                                      timeStamp.setTextAppearance(getApplicationContext(), R.style.AppTheme);
                                                  } else {
                                                      timeStamp.setTextAppearance(R.style.AppTheme);
                                                  }
                                                  timeStamp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                                                  dateTimeRow.addView(timeStamp);

                                                  tableLayout.addView(dateTimeRow);
                                                  tableLayout.addView(tableRow);
                                                  messageText.setText("");

                                                  if (LogInActivity.loggedInUserType) {
                                                      ChooseConversationLecturerActivity.saveKey("", getApplicationContext());
                                                  } else {
                                                      ChooseConversationStudentActivity.saveKey("", getApplicationContext());
                                                  }

                                                  tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                      @Override
                                                      public boolean onLongClick(View v) {
                                                          highlightRow(tableRow);
                                                          return true;
                                                      }
                                                  });

                                                  scrollView.fullScroll(View.FOCUS_DOWN);

                                                  String key_;
                                                  if(LogInActivity.loggedInUserType)
                                                  {
                                                      key_ = ChooseConversationLecturerActivity.chosenConvoKey;
                                                  }
                                                  else
                                                  {
                                                      key_ = ChooseConversationStudentActivity.chosenConvoKey;
                                                  }

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

    public void setViews()
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
                        String type;
                        if (LogInActivity.loggedInUserType) {
                            type = ChooseConversationLecturerActivity.chosenConvoKey;
                        } else {
                            type = ChooseConversationStudentActivity.chosenConvoKey;
                        }
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
            if(allSenders != null){

                final TableRow tableRow = new TableRow(getApplicationContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.2f));

                if(allSenders.get(i).equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    tableRow.setBackgroundResource(R.drawable.bubble_green);
                }
                else {
                    tableRow.setBackgroundResource(R.drawable.bubble_yellow);
                }

                final TextView senderInitial = new TextView(getApplicationContext());
                senderInitial.setText(allSenders.get(i));
                senderInitial.setPadding(0, 0, 0, 0);
                senderInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.6f));
                if (Build.VERSION.SDK_INT < 23) {
                    senderInitial.setTextAppearance(getApplicationContext(), R.style.senderTextStyle);
                } else {
                    senderInitial.setTextAppearance(R.style.senderTextStyle);
                }

                final TextView messageInitial = new TextView(getApplicationContext());
                messageInitial.setText(allMessages.get(i));
                messageInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
                messageInitial.setTextAppearance(getApplicationContext(), R.style.messageTextStyle);

                tableRow.setLongClickable(true);
                tableRow.setGravity(Gravity.CENTER);

                tableRow.addView(senderInitial);
                tableRow.addView(messageInitial);

                final TableRow dateTimeRow = new TableRow(getApplicationContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                final TextView timeStamp = new TextView(getApplicationContext());
                timeStamp.setText(allTimeStamps.get(i));
                timeStamp.setPadding(20,0,0,0);
                timeStamp.setTextAppearance(getApplicationContext(), R.style.AppTheme);
                timeStamp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                dateTimeRow.addView(timeStamp);

                tableLayout.addView(dateTimeRow);
                tableLayout.addView(tableRow);

                tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for(int g =0; g < tableLayout.getChildCount();g++) {
                            if(v == tableLayout.getChildAt(g)) {
                                highlightRow(tableRow);
                                deletePos = (g-1)/2;
                                tablePos = (deletePos*2)+1;
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

                                //CHECKS FOR ADMIN DELETE ACCESS GO HERE
                                if(LogInActivity.loggedInUser.equalsIgnoreCase(groupAdmin)) {
                                    openPopUpDeleteWindow();
                                }
                                else {
                                    if(LogInActivity.loggedInUser.equalsIgnoreCase(((TextView)((TableRow) tableLayout.getChildAt(tablePos)).getChildAt(0)).getText().toString()))
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
                if(((TextView)((TableRow) tableLayout.getChildAt(tablePos)).getChildAt(0)).getText().toString().equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    row.setBackgroundResource(R.drawable.bubble_green);
                }
                else
                {
                    row.setBackgroundResource(R.drawable.bubble_yellow);
                }
                deleteIcon.setClickable(false);
                ((ViewManager) row.getParent()).removeView(deleteIcon);
                row.setGravity(Gravity.CENTER);
                row.removeView(deleteIcon);
            }
            highlightedRows.clear();
        }
        else if(numPressed == 1)// && !wasHighlighted)
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
                                        openConfirmLeaveGroupPopUp(tableLayout);
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
            setViews();
        }
    };

    public void openConfirmLeaveGroupPopUp(TableLayout d)
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

        //TODO see if this works
        final PopupWindow pwindo = new PopupWindow(layout, width-(width/4), height-(height/4), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        final String userNameToRemove = ((TextView) ((TableRow) d.getChildAt(personDeleteIndex)).getChildAt(0)).getText().toString();

        TextView text2 = (TextView) layout.findViewById(R.id.question_info);
        text2.setText("Are you sure you want to remove " + userNameToRemove + " from this conversation?");

        Button confirmLeave = (Button) layout.findViewById(R.id.delete_info);
        confirmLeave.setText("Leave Group");
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
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                        if(user != null) {
                            c.RemoveUserFromGroup(new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    Toast.makeText(getApplicationContext(), "Group left.", Toast.LENGTH_LONG).show();
                                    Intent f = new Intent(getApplicationContext(), ChooseConversationLecturerActivity.class);
                                    startActivity(f);
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
}