package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hooper.kenneth.academicassistant.notifications.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Conversation;
import model.ConversationServiceConnectivity;
import model.Message;
import model.ServerCallback;
import model.User;
import model.UserServiceConnectivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChooseConversationActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private ConversationServiceConnectivity c;
    private UserServiceConnectivity u;
    public static String chosenConvoKey;
    public static String chosenGroupName;
    private ArrayList<Conversation> conversations;
    private ArrayList<TableRow> highlightedRows;
    private ArrayList<Integer> messageIDs;
    private int numPressed = 0;
    private boolean wasHighlighted = false;
    private ImageView deleteIcon;
    private ImageView leaveGroupIcon;
    private String admin;
    private String deleteKey;
    ProgressDialog pDialog;
    private static final String TAG = "LecturerConvo";

    private GoogleCloudMessaging gcm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conversation_lecturer);

        pDialog =  new ProgressDialog(this);
        c = new ConversationServiceConnectivity(getApplicationContext(), pDialog);
        u = new UserServiceConnectivity(getApplicationContext(), pDialog);
        saveKey("", getApplicationContext());
        chosenConvoKey = "";
        chosenGroupName = "";

        gcm = GoogleCloudMessaging.getInstance(this);

        tableLayout = (TableLayout) findViewById(R.id.convos);
        tableLayout.setVerticalScrollBarEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Toolbar congifuration
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.conv_heading);
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        highlightedRows = new ArrayList<>();

        fillConvos();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new RegisterInBackgroundTask(getApplicationContext()).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(LogInActivity.loggedInUserType) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else
        {
            getMenuInflater().inflate(R.menu.main_menu_stu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miInfo:
                openPopUpWindow();
                return true;
            case R.id.miCreateGroup:
                Intent f = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(f);
                finish();
                return true;
            case R.id.miJoinGroup:
                Intent l = new Intent(getApplicationContext(), JoinGroupActivity.class);
                startActivity(l);
                finish();
                return true;
            case R.id.miLogout:
                //when log out set saved data to be null
                LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUserType("loggedInUserType.txt", false, getApplicationContext());
                LogInActivity.savePassword("password.txt", "", getApplicationContext());
                saveKey("", getApplicationContext());
                saveStartUpNumber("0", getApplicationContext());

                Intent e = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(e);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Actions when button is pressed etc
    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_SCROLL: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    //display any existing conversations
    public void fillConvos() {
        c.getConversationsForUser(new ServerCallback() {
                                      @Override
                                      public void onSuccess(JSONArray response) {

                                          conversations = new ArrayList<>();
                                          final ArrayList<Conversation> convos = new ArrayList<>();
                                          try {
                                              for (int i = 0; i < response.length(); i++) {

                                                  JSONObject res = (JSONObject) response.get(i);

                                                  String key;
                                                  String conversationName;
                                                  String administrator;
                                                  ArrayList<User> members = new ArrayList<>();
                                                  ArrayList<Message> messages = new ArrayList<>();

                                                  key = res.getString("Key");
                                                  conversationName = res.getString("ConversationName");
                                                  administrator = res.getString("Administrator");

                                                  JSONArray users = res.getJSONArray("Users");
                                                  if (users != null) {
                                                      for (int j = 0; j < users.length(); j++) {
                                                          JSONObject users2 = (JSONObject) users.get(j);

                                                          String email;
                                                          String password;
                                                          boolean admin;

                                                          email = users2.getString("Email");
                                                          password = users2.getString("Password");
                                                          admin = users2.getBoolean("Admin");

                                                          User u = new User(email, password, admin, convos);

                                                          members.add(u);
                                                      }
                                                  }

                                                  JSONArray mess = res.getJSONArray("Messages");
                                                  if (mess != null) {
                                                      for (int j = 0; j < mess.length(); j++) {
                                                          JSONObject mess2 = (JSONObject) mess.get(j);

                                                          int id = mess2.getInt("MessageID");
                                                          String content = mess2.getString("MessageContent");
                                                          String recipient = mess2.getString("Recipient");
                                                          String sender = mess2.getString("Sender");
                                                          String timeStamp = mess2.getString("TimeStamp");
                                                          String CKey = mess2.getString("ConversationKey");

                                                          Message u = new Message(id, content, recipient, sender, timeStamp, CKey);

                                                          messages.add(u);
                                                      }
                                                  }


                                                  Conversation con = new Conversation(key, conversationName, administrator, members, messages);
                                                  conversations.add(con);
                                              }
                                              if (conversations != null) {
                                                  for (int i = 0; i < conversations.size(); i++) {
                                                      for (int j = 0; j < conversations.get(i).getMembers().size(); j++) {
                                                          if (conversations.get(i).getMembers().get(j).getEmail().equalsIgnoreCase(LogInActivity.loggedInUser)) {
                                                              if (convos.contains(conversations.get(i))) {
                                                                  System.out.println("");
                                                              } else {
                                                                  convos.add(conversations.get(i));
                                                              }
                                                          }
                                                      }
                                                  }
                                              }
                                              for (int i = 0; i < convos.size(); i++) {
                                                  final TableRow tableRow = new TableRow(getApplicationContext());
                                                  tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50));
                                                  tableRow.setBackgroundResource(R.drawable.corners);
                                                  tableRow.setPadding(20, 20, 20, 20);
                                                  tableRow.setGravity(Gravity.CENTER);

                                                  final TextView conv = new TextView(getApplicationContext());
                                                  conv.setText(convos.get(i).getConversationName());
                                                  conv.setTextAppearance(getApplicationContext(), R.style.chat);
                                                  conv.setShadowLayer(10, 3, 3, Color.BLACK);
                                                  tableRow.setClickable(true);
                                                  tableRow.setLongClickable(true);

                                                  buttonEffect(tableRow);

                                                  tableRow.addView(conv);
                                                  tableLayout.addView(tableRow);

                                                  final int f = i;

                                                  tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                      @Override
                                                      public boolean onLongClick(View v) {
                                                          highlightRow(tableRow);
                                                          return true;
                                                      }
                                                  });

                                                  tableRow.setOnClickListener(new View.OnClickListener() {
                                                      public void onClick(View v) {

                                                          //when a row is selected, store the key to that conversation to load the information and messages on next activity

                                                          saveKey(convos.get(f).getKey(), getApplicationContext());
                                                          chosenConvoKey = convos.get(f).getKey();
                                                          chosenGroupName = convos.get(f).getConversationName();

                                                          Intent i = new Intent(getApplicationContext(), ViewMessagesActivity.class);
                                                          startActivity(i);
                                                      }
                                                  });
                                              }

                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                              Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                          }
                                      }

                                      @Override
                                      public void onSuccess(JSONObject result) {

                                      }

                                      public void onSuccess(String result) {

                                      }

                                      @Override
                                      public void onError(VolleyError error) {
                                          Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                      }
                                  }, LogInActivity.loggedInUser
        );
    }

    //Change background and add delete and leave icons to a row when highlighted
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

                leaveGroupIcon = new ImageView(this);
                leaveGroupIcon.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
                leaveGroupIcon.setPadding(20, 0, 0, 0);
                leaveGroupIcon.setClickable(true);
                row.addView(leaveGroupIcon);

                wasHighlighted = true;

                leaveGroupIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < tableLayout.getChildCount(); i++) {
                            //get text on text view which is on the table row
                            if (((TextView) ((TableRow) tableLayout.getChildAt(i)).getChildAt(0)).getText().toString().equalsIgnoreCase(((TextView) highlightedRows.get(0).getChildAt(0)).getText().toString())) {
                                admin = conversations.get(i).getAdministrator();
                                deleteKey = conversations.get(i).getKey();
                            }
                        }
                        if (LogInActivity.loggedInUser.equalsIgnoreCase(admin)) {

                            Toast.makeText(getApplicationContext(), "As group admin you cannot leave. You must delete the group to leave.", Toast.LENGTH_LONG).show();

                        } else {
                            openConfirmLeaveGroupPopUp();
                        }
                    }
                });

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //IF USER IS GROUP ADMIN THEN CAN DELETE
                        for (int i = 0; i < tableLayout.getChildCount(); i++) {
                            //get text on text view which is on the table row
                            if (((TextView) ((TableRow) tableLayout.getChildAt(i)).getChildAt(0)).getText().toString().equalsIgnoreCase(((TextView) highlightedRows.get(0).getChildAt(0)).getText().toString())) {
                                admin = conversations.get(i).getAdministrator();
                                deleteKey = conversations.get(i).getKey();
                            }
                        }
                        if (LogInActivity.loggedInUser.equalsIgnoreCase(admin)) {

                            openConfirmDeletePopUp();

                        } else {
                            Toast.makeText(getApplicationContext(), "Only the group admin can delete this group", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                numPressed = 0;
            }
        }
    }

    public void openConfirmLeaveGroupPopUp()
    {
        //Open pop up window

        LayoutInflater inflater = (LayoutInflater) ChooseConversationActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_conversation_delete_popup,
                (ViewGroup) findViewById(R.id.glayout2));

        //get the size of screen on device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //make view one quater squared the size of the screen
        final PopupWindow pwindo = new PopupWindow(layout, width-(width/4), height-(height/2), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        TextView text = (TextView) layout.findViewById(R.id.question_info);
        text.setText(R.string.confirm_leave_group);

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
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                            hidepDialog();
                        }
                        if(user != null) {
                            c.RemoveUserFromGroup(new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    Toast.makeText(getApplicationContext(), "Group left.", Toast.LENGTH_LONG).show();
                                    hidepDialog();
                                    Intent f = new Intent(getApplicationContext(), ChooseConversationActivity.class);
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
                                    hidepDialog();
                                }
                            }, deleteKey, user);
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
                }, LogInActivity.loggedInUser);

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


    public void openConfirmDeletePopUp()
    {
        LayoutInflater inflater = (LayoutInflater) ChooseConversationActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_conversation_delete_popup,
                (ViewGroup) findViewById(R.id.glayout2));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final PopupWindow pwindo = new PopupWindow(layout, width-(width/4), height-(height/2), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        Button confirmDelete = (Button) layout.findViewById(R.id.delete_info);
        confirmDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                c.getConversation(new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        messageIDs = new ArrayList<>();
                        try {
                            JSONArray mess = result.getJSONArray("Messages");
                            if (mess != null) {
                                for (int j = 0; j < mess.length(); j++) {
                                    JSONObject mess2 = (JSONObject) mess.get(j);

                                    int id = mess2.getInt("MessageID");

                                    messageIDs.add(id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(messageIDs.size() == 0)
                        {
                            c.deleteConversation(new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                }

                                @Override
                                public void onSuccess(JSONArray result) {
                                }

                                @Override
                                public void onSuccess(String result) {
                                    Toast.makeText(getApplicationContext(), "Conversation Deleted", Toast.LENGTH_LONG).show();
                                    hidepDialog();
                                    Intent f = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                                    startActivity(f);
                                    finish();
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Error deleting convseration..", Toast.LENGTH_LONG).show();
                                    hidepDialog();
                                }
                            }, deleteKey);
                        }
                        else if(messageIDs.size() > 0)
                        {
                            for (int i = 0; i < messageIDs.size(); i++) {
                                final int f = i;
                                //delete all messages in conversation before deleting the conversation
                                c.deleteMessagesInConvo(new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                    }

                                    @Override
                                    public void onSuccess(JSONArray result) {
                                    }

                                    @Override
                                    public void onSuccess(String result) {

                                        if (messageIDs.get(f) == messageIDs.get(messageIDs.size() - 1)) {
                                            c.deleteConversation(new ServerCallback() {
                                                @Override
                                                public void onSuccess(JSONObject result) {
                                                }

                                                @Override
                                                public void onSuccess(JSONArray result) {
                                                }

                                                @Override
                                                public void onSuccess(String result) {
                                                    Toast.makeText(getApplicationContext(), "Conversation Deleted", Toast.LENGTH_LONG).show();
                                                    hidepDialog();
                                                    Intent f = new Intent(getApplicationContext(), ChooseConversationActivity.class);
                                                    startActivity(f);
                                                    finish();
                                                }

                                                @Override
                                                public void onError(VolleyError error) {
                                                    Toast.makeText(getApplicationContext(), "Error deleting conversation..", Toast.LENGTH_LONG).show();
                                                    hidepDialog();
                                                }
                                            }, deleteKey);
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Failed to delete group", Toast.LENGTH_LONG).show();
                                        hidepDialog();
                                    }
                                }, messageIDs.get(f));

                            }
                        }
                        pwindo.dismiss();
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
                }, deleteKey);
            }
        });
        buttonEffect(confirmDelete);

        Button cancelDelete = (Button) layout.findViewById(R.id.cancel_info);
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwindo.dismiss();
            }
        });
        buttonEffect(cancelDelete);
    }

    //to un-highlight a row
    @Override
    public void onBackPressed()
    {
        numPressed++;
        if(numPressed == 1 && wasHighlighted) {
            //set row bacl to non highlighted state
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                View view = tableLayout.getChildAt(i);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    row.setBackgroundResource(R.drawable.corners);
                    deleteIcon.setClickable(false);
                    ((ViewManager) row.getParent()).removeView(deleteIcon);
                    ((ViewManager) row.getParent()).removeView(leaveGroupIcon);
                    row.setPadding(20, 20, 20, 20);
                    row.setGravity(Gravity.CENTER);
                    row.removeView(deleteIcon);
                    row.removeView(leaveGroupIcon);
                }
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

    //Get information about the conversation
    public void openPopUpWindow()
    {
        LayoutInflater inflater = (LayoutInflater) ChooseConversationActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_conversation_info_popup,
                (ViewGroup) findViewById(R.id.glayout1));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final PopupWindow pwindo = new PopupWindow(layout, (width-(width/4)), (height-(height/2)), true);

        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        pwindo.setBackgroundDrawable(new BitmapDrawable());

        TextView loggedIn = (TextView) pwindo.getContentView().findViewById(R.id.loggedDisplay_info);
        loggedIn.setText(LogInActivity.loggedInUser);
        TextView type = (TextView) pwindo.getContentView().findViewById(R.id.typeDisplay_info);
        if (LogInActivity.loggedInUserType) {
            type.setText(R.string.lecturer);
        } else {
            type.setText(R.string.student);
        }

        Button closePopup = (Button) layout.findViewById(R.id.okButtonlog_info);
        closePopup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwindo.dismiss();
            }

        });
        buttonEffect(closePopup);
    }

    //save the key to file so it can be accessed in the future
    public static void saveKey(String key, Context ctx)
    {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("UniqueKey.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(key);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void saveStartUpNumber(String num, Context ctx)
    {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("StartUpNumber.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(num);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //register device with GCM
    private class RegisterInBackgroundTask extends AsyncTask<String,String,String> {
        private Context context;
        private String SENDER_ID = "113571816922";
        private String registrationId;

        public RegisterInBackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            String message = "";
            try {
                if (gcm == null) {gcm = GoogleCloudMessaging.getInstance(context);}
                registrationId = gcm.register(SENDER_ID);
                Log.d(TAG, message);
                sendRegistrationIdToBackend(registrationId);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String msg) {
        }

        private void sendRegistrationIdToBackend(String registrationId) {

            String backendBaseUrl = "http://academicassistantservice2.azurewebsites.net";

            PushNotificationClient client = new PushNotificationClient(backendBaseUrl);

            Device device = createDevice(registrationId);

            client.registerDevice(device, new Callback<Device>() {
                @Override
                public void success(Device device, Response response) {
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.d("", ""+retrofitError);
                }
            });

            Log.i(TAG, registrationId);
        }

        private Device createDevice(String registrationId) {
            Device device = new Device();
            device.Platform = "Android";
            device.Token = registrationId;
            device.UserName = LogInActivity.loggedInUser;
            device.DeviceGuid = null;
            device.SubscriptionCategories = new ArrayList<>();
            device.SubscriptionCategories.add(device.UserName);
            return device;
        }
    }

    public void unregister() {
        try {
            gcm.unregister();
        } catch(IOException d) {
            d.printStackTrace();
        }
    }

    //turn progress dialog off
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}