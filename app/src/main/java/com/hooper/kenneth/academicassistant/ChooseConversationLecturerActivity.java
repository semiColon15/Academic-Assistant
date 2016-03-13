package com.hooper.kenneth.academicassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Conversation;
import model.ConversationServiceConnectivity;
import model.Message;
import model.ServerCallback;
import model.User;
import model.VolleyResponseListener;

public class ChooseConversationLecturerActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private List<TableRow> rows;
    private Button logOut;
    private Button addGroup;
    private ConversationServiceConnectivity c;
    public static String chosenConvoKey;

    private ArrayList<Conversation> conversations;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conversation_lecturer);
        c = new ConversationServiceConnectivity(getApplicationContext());
        saveKey("", getApplicationContext());
        chosenConvoKey = "";

        tableLayout = (TableLayout) findViewById(R.id.convos);
        tableLayout.setVerticalScrollBarEnabled(true);
        logOut = (Button) findViewById(R.id.logout);
        addGroup = (Button) findViewById(R.id.addGroup);
        rows = new ArrayList<TableRow>();

        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                LogInActivity.savePassword("password.txt", "", getApplicationContext());
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);
                finish();
            }
        });

        addGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(i);
                finish();
            }
        });

        fillConvos();
    }

    //TODO Change method to take make request to service to search for conversations for a specific user, not all conversations.
    //Write method in service to accept one string parameter "email" the  LINQ query to get conversations for that user
    public void fillConvos() {

        c.getExistingConversations(new ServerCallback() {
                                       @Override
                                       public void onSuccess(JSONArray response) {
                                            conversations = new ArrayList<>();
                                           try
                                           {
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
                                                   if(users != null) {
                                                       for (int j = 0; j < users.length(); j++) {
                                                           JSONObject users2 = (JSONObject) users.get(j);

                                                           String email;
                                                           String password;
                                                           boolean admin;
                                                           ArrayList<Conversation> convos = new ArrayList<>();

                                                           email = users2.getString("Email");
                                                           password = users2.getString("Password");
                                                           admin = users2.getBoolean("Admin");

                                                           User u = new User(email, password, admin, convos);

                                                           members.add(u);
                                                       }
                                                   }

                                                   JSONArray mess = res.getJSONArray("Messages");
                                                   if(mess != null) {
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


                                                   Conversation con = new Conversation(key, conversationName,administrator, members, messages);
                                                   conversations.add(con);

                                                   System.out.print("CONVERSATIONS " + con.getKey() + ", " + con.getConversationName());

                                                   final TableRow tableRow = new TableRow(getApplicationContext());
                                                   tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50));
                                                   tableRow.setBackgroundResource(R.drawable.corners);
                                                   tableRow.setPadding(20, 20, 20, 20);
                                                   tableRow.setGravity(Gravity.CENTER);

                                                   final TextView message = new TextView(getApplicationContext());
                                                   message.setText(conversations.get(i).getConversationName());
                                                   message.setTextAppearance(getApplicationContext(), R.style.chat);
                                                   tableRow.setClickable(true);

                                                   tableRow.addView(message);
                                                   rows.add(tableRow);
                                                   tableLayout.addView(tableRow);

                                                   final int f = i;
                                                   tableRow.setOnClickListener(new View.OnClickListener() {
                                                       public void onClick(View v) {
                                                           saveKey(conversations.get(f).getKey(), getApplicationContext());
                                                           chosenConvoKey = retrieveKey();
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
                                   }
        );
    }

    //THIS METHOD SHOULD NOT BE NEEDED WHEN ABOVE METHOD IS FIXED
    /*public ArrayList<Conversation> getConversationsForUser(String email)
    {
        ArrayList<Conversation> convos = new ArrayList<>();
        if (retrieveAllConversations() != null) {
            for (int i = 0; i < retrieveAllConversations().size(); i++) {
                for (int j = 0; j < retrieveAllConversations().get(i).getMembers().size(); j++) {
                    if (retrieveAllConversations().get(i).getMembers().get(j).getEmail().equalsIgnoreCase(email) || retrieveAllConversations().get(i).getAdministrator().equalsIgnoreCase(email)) {
                        if (convos.contains(retrieveAllConversations().get(i))) {
                        } else {
                            convos.add(retrieveAllConversations().get(i));
                        }
                    }
                }
            }
        }
        //}
        return convos;
    }*/

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
