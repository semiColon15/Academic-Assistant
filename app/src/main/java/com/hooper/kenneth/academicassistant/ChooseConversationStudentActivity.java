package com.hooper.kenneth.academicassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Conversation;

public class ChooseConversationStudentActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private List<TableRow> rows;
    private Button logOut;
    private ArrayList<Conversation> convos;

    private String[] tempNames = { "Joe Smith", "College Group", "Leanne Quinn", "John Keogh", "Neil Patrick Harris", "Frank Sinatra", "Paul O'Reilly", "Alan Brogan", "Michael McDonnell", "Pete Sampras"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conversation_student);

        tableLayout = (TableLayout) findViewById(R.id.convos_stu);
        tableLayout.setVerticalScrollBarEnabled(true);
        logOut = (Button) findViewById(R.id.logout_stu);

        rows = new ArrayList<TableRow>();

        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogInActivity.saveToken("token.txt", "", getApplicationContext());
                LogInActivity.saveLoggedInUser("loggedInUser.txt", "", getApplicationContext());
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);
                finish();
            }
        });

        fillConvos();
    }

    public void fillConvos() {

        for(int i = 0; i < 10; i++)
        {
            final TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50));
            tableRow.setBackgroundResource(R.drawable.corners);
            tableRow.setPadding(20, 20, 20, 20);
            tableRow.setGravity(Gravity.CENTER);

            final TextView message = new TextView(getApplicationContext());
            message.setText(tempNames[i]);
            message.setTextAppearance(getApplicationContext(), R.style.chat);
            tableRow.setClickable(true);

            tableRow.addView(message);
            rows.add(tableRow);
            tableLayout.addView(tableRow);

            tableRow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ViewMessagesActivity.class);
                    startActivity(i);
                }
            });
        }
    }
}
