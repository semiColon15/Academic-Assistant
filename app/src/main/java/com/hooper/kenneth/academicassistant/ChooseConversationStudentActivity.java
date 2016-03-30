package com.hooper.kenneth.academicassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Conversation;
import model.ConversationServiceConnectivity;
import model.Message;
import model.ServerCallback;
import model.User;

public class ChooseConversationStudentActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private ConversationServiceConnectivity c;
    public static String chosenConvoKey;
    public static String chosenGroupName;
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private ArrayList<Conversation> conversations;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conversation_student);

        pDialog =  new ProgressDialog(this);
        c = new ConversationServiceConnectivity(getApplicationContext(), pDialog);
        saveKey("", getApplicationContext());
        chosenConvoKey = "";
        chosenGroupName = "";

        tableLayout = (TableLayout) findViewById(R.id.convos_stu);
        tableLayout.setVerticalScrollBarEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.chatify);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Conversations");
        mTitle.setShadowLayer(10, 5, 5, Color.BLACK);

        fillConvos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_stu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miInfo_stu:
                //Intent i = new Intent(getApplicationContext(), JoinGroupActivity.class);
                //startActivity(i);
                //finish();
                return true;
            case R.id.miJoinGroup_stu:
                Intent l = new Intent(getApplicationContext(), JoinGroupActivity.class);
                startActivity(l);
                //finish();
                return true;
            case R.id.miLogout_stu:
                LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                LogInActivity.savePassword("password.txt", "", getApplicationContext());
                saveKey("", getApplicationContext());
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

    public void fillConvos() {

        c.getExistingConversations(new ServerCallback() {

                                       @Override
                                       public void onSuccess(JSONArray response) {

                                           conversations = new ArrayList<>();
                                           final ArrayList<Conversation> convos = new ArrayList<Conversation>();
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

                                                           int id;
                                                           String content;
                                                           String recipient;
                                                           String sender;
                                                           String CKey;

                                                           id = mess2.getInt("MessageID");
                                                           content = mess2.getString("MessageContent");
                                                           recipient = mess2.getString("Recipient");
                                                           sender = mess2.getString("Sender");
                                                           CKey = mess2.getString("ConversationKey");

                                                           Message u = new Message(id, content, recipient, sender, CKey);

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
                                                   buttonEffect(tableRow);

                                                   tableRow.addView(conv);
                                                   tableLayout.addView(tableRow);

                                                   final int f = i;
                                                   tableRow.setOnClickListener(new View.OnClickListener() {
                                                       public void onClick(View v) {
                                                           saveKey(convos.get(f).getKey(), getApplicationContext());
                                                           chosenConvoKey = retrieveKey();
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
                                   }
        );
    }

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

    public String retrieveKey() {
        String key = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/UniqueKey.txt"));
            String s = br.readLine();
            System.out.println("IN GETTING METHOD::::::::: " + s);
            char[] p = s.toCharArray();
            for (int i = 7; i < s.length(); i++) {
                key += p[i];
                System.out.println("TRANSFERRING::::::: " + key);
            }

            System.out.println("SOSN USER: " + key);
            System.out.println("ORIGINAL:::::: " + s);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }
}
