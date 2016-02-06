package com.hooper.kenneth.academicassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseConversationActivity extends Activity {

    private TableLayout tableLayout;
    private List<TableRow> rows;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conversation);

        tableLayout = (TableLayout) findViewById(R.id.convos);
        tableLayout.setVerticalScrollBarEnabled(true);

        rows = new ArrayList<TableRow>();

        fillConvos();
    }

    public void fillConvos() {

        for(int i = 0; i < 15; i++)
        {
            final TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50));
            tableRow.setBackgroundResource(R.drawable.corners);
            tableRow.setPadding(20, 20, 20, 20);
            tableRow.setGravity(Gravity.CENTER);

            final TextView message = new TextView(getApplicationContext());
            message.setText("User");
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
