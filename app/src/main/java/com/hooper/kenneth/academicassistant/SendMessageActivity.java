package com.hooper.kenneth.academicassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import model.Message;

public class SendMessageActivity extends Activity {

    private Button sendButton;
    private static Button viewMessagesButton;
    private EditText id;
    private EditText content;
    private EditText recipient;
    private EditText sender;
    private static SendMessageActivity sInstance;
    private RequestQueue mRequestQueue;
    private String jsonResponse;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        sendButton = (Button) findViewById(R.id.send);
        viewMessagesButton = (Button) findViewById(R.id.viewMessages);
        content = (EditText) findViewById(R.id.content);
        recipient = (EditText) findViewById(R.id.recipient);
        sender = (EditText) findViewById(R.id.sender);

        sInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        setViewMessagesButton();

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSendButton2(content.getText().toString(), recipient.getText().toString(), sender.getText().toString());
            }
        });
    }

            public void setViewMessagesButton() {
                viewMessagesButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), ViewMessagesActivity.class);
                        startActivity(i);
                    }
                });
            }

            //POST
            public void setSendButton(final String id, final String content, final String recipient, final String sender) {

                //Message msg = new Message(id, content, recipient, sender);
                sendButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final String URL = "http://academicassistant2.azurewebsites.net/api/Message/PostMessage/?";
                        // Post params to be sent to the server
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("id", id);
                        params.put("content", content);
                        params.put("recipient", recipient);
                        params.put("sender", sender);

                        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            VolleyLog.v("Response:%n %s", response.toString(4));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "ERROR 1", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error 2: ", error.getMessage());
                                Toast.makeText(getApplicationContext(), "ERROR 2", Toast.LENGTH_LONG).show();
                            }
                        });

// add the request object to the queue to be executed
                        mRequestQueue.add(req);
                    }
                });
            }

            public void setSendButton2(String content, String recipient, String sender) {

                String baseUrl = "http://academicassistant2.azurewebsites.net/api/Message/PostMessage/?";
                mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                String url = baseUrl + "content=" + content + "&recipient=" + recipient + "&sender=" + sender;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                    }
                });
                mRequestQueue.add(stringRequest);
            }

        }