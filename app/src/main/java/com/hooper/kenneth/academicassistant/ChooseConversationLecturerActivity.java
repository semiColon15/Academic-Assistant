package com.hooper.kenneth.academicassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Conversation;
import model.ConversationServiceConnectivity;

public class ChooseConversationLecturerActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private List<TableRow> rows;
    private Button logOut;
    private Button addGroup;
    private ConversationServiceConnectivity c;
    public static String chosenConvoKey;

    ///private String[] tempNames = { "Joe Smith", "College Group", "Leanne Quinn", "John Keogh", "Neil Patrick Harris", "Frank Sinatra", "Paul O'Reilly", "Alan Brogan", "Michael McDonnell", "Pete Sampras"};
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
                /*LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                LogInActivity.savePassword("password.txt", "", getApplicationContext());
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);
                finish();*/
                fillConvos();
            }
        });

        addGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(i);
                finish();
            }
        });


        //fillConvos();
    }

    public void fillConvos() {
        conversations = c.getConversationsForUser(LogInActivity.loggedInUser);
        for(int i = 0; i < conversations.size(); i++)
        {
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

    public String retrieveKey()
    {
        String key = "";
        try {
            File myDir = new File(getFilesDir().getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(myDir + "/UniqueKey.txt"));
            String s = br.readLine();
            System.out.println("IN GETTING METHOD::::::::: " + s);
            char[] p = s.toCharArray();
            for(int i = 7; i < s.length(); i++)
            {
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
