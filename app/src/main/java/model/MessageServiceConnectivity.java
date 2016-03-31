package model;

import java.util.Map;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.ChooseConversationLecturerActivity;
import com.hooper.kenneth.academicassistant.ChooseConversationStudentActivity;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.R;
import com.hooper.kenneth.academicassistant.ViewMessagesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MessageServiceConnectivity {

    private ViewMessagesActivity sInstance;
    private RequestQueue mRequestQueue;
    private String[] allSenders;
    private String[] allMessages;
    private ProgressDialog pDialog;
    private Context context;
    private TableLayout tableLayout;

    UserServiceConnectivity userServiceConnectivity;

    private static String TAG = ViewMessagesActivity.class.getSimpleName();
    final String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/";

    public MessageServiceConnectivity(Context c, ViewMessagesActivity vma, ProgressDialog pDialog, TableLayout tableLayout)
    {
        context = c;
        sInstance = vma;
        this.pDialog = pDialog;
        mRequestQueue = Volley.newRequestQueue(c);
        setAllSendersAndAllMessages();
        this.tableLayout = tableLayout;
        userServiceConnectivity = new UserServiceConnectivity(context, pDialog);
    }

    public void setInitialViews()
    {
        for (int i = 0; i < allMessages.length; i++) {
            if(allSenders[i] != null){

                final TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                if(allSenders[i].equalsIgnoreCase(LogInActivity.loggedInUser)) {
                    tableRow.setBackgroundResource(R.drawable.bubble_green);
                }
                else
                {
                    tableRow.setBackgroundResource(R.drawable.bubble_yellow);
                }

                final TextView senderInitial = new TextView(context);
                senderInitial.setText(allSenders[i] + "\t");
                senderInitial.setPadding(0, 0, 10, 0);
                senderInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                senderInitial.setTextAppearance(context, R.style.senderTextStyle);

                final TextView messageInitial = new TextView(context);
                messageInitial.setText(allMessages[i]);
                messageInitial.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                messageInitial.setTextAppearance(context, R.style.messageTextStyle);

                tableRow.addView(senderInitial);
                tableRow.addView(messageInitial);

                tableLayout.addView(tableRow);
                sInstance.addRow(tableRow);
            }
        }
    }

    public void setAllSendersAndAllMessages()
    {
        String url = "Messages/GetMessages";
        showpDialog();
        String Url = baseUrl + url;


        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            allSenders = new String[response.length()];
                            allMessages = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject message = (JSONObject) response.get(i);
                                String key = message.getString("ConversationKey");
                                /*System.out.println("Conversation KEY: *&*&*&*&*& " + key);
                                System.out.println("Conversation saved key" + ChooseConversationLecturerActivity.chosenConvoKey);*/
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
                                    allSenders[i] = message.getString("Sender");
                                    //System.out.println("SETTING SENDER " + allSenders[i]);
                                    allMessages[i] = message.getString("MessageContent");
                                    //System.out.println("SETTING MEssage " + allMessages[i]);
                                }
                            }
                            setInitialViews();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                String token = LogInActivity.token;
                headers.put("Authorization", "bearer "+ token);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }

    public void setViews()
    {
        String url = "Messages/GetMessages";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            String jsonResponse = "";
                            String senderText = "";
                            String contentText = "";
                            String content = null;
                            String sender = null;
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject message = (JSONObject) response
                                        .get(i);

                                content = message.getString("messageContent");
                                sender = message.getString("sender");

                                senderText += sender + ":\n";
                                contentText += content + "\n";
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        // Adding request to request queue
        mRequestQueue.add(req);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void SendMessage(String content, String recipient, String sender, String convoKey)
    {
        String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/Messages/PostMessage/?";
        HashMap<String, String> params = new HashMap<>();
        params.put("MessageID", "1");
        params.put("MessageContent", content);
        params.put("Recipient", recipient);
        params.put("Sender", sender);
        params.put("ConversationKey", convoKey);

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                            setViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ERRORRR", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Volley error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }
}
