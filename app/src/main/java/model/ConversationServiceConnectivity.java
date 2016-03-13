package model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConversationServiceConnectivity {

    private Context context;
    private RequestQueue mRequestQueue;

    private String baseUrl = "http://academicassistantservice.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    private String[] allConversationKeys;
    public static ArrayList<Conversation> allConversations;

    public ConversationServiceConnectivity(Context context)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        getExistingConversationKeys();
        //getExistingConversations(ServerCallback c);
    }

    public void getExistingConversationKeys()
    {
            String url = "Conversations/GetConversations";
            String Url = baseUrl + url;

            JsonArrayRequest req = new JsonArrayRequest(Url,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());

                            try {

                                allConversationKeys = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject emails = (JSONObject) response.get(i);

                                    allConversationKeys[i] = emails.getString("Key");
                                    System.out.println("CONVO KEYS: " + allConversationKeys[i]);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  headers = new HashMap<>();
                    String token = LogInActivity.token;
                    headers.put("Authorization", "bearer " + token);
                    return headers;
                }
            };

            // Adding request to request queue
            mRequestQueue.add(req);
    }

    public String[] retrieveAllConversationKeys()
    {
        return allConversationKeys;
    }


    public void getExistingConversations(final ServerCallback callback)
    {
        String url = "Conversations/GetConversations";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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

        // Adding request to request queue
        mRequestQueue.add(req);
    }

    public ArrayList<Conversation> retrieveAllConversations()
    {
        return allConversations;
    }


    public void CreateConversation(String key, String name, String admin)
    {
        String baseUrl = "http://academicassistantservice.azurewebsites.net/api/Conversations/PostConversation?";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Key", key);
        params.put("ConversationName", name);
        params.put("Administrator", admin);

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Successfully created group", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ERROR creating group", Toast.LENGTH_LONG).show();
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
                String token = LogInActivity.token;
                headers.put("Authorization", "bearer "+ token);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }

    public void AddUserIntoConversation(String key, String email, String password, String admin)
    {
        String baseUrl = "http://academicassistantservice.azurewebsites.net/api/Conversations/PostAddUser?key=" + key;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Email", email);
        params.put("Password", password);
        params.put("Amin", admin);

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
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
                String token = LogInActivity.token;
                headers.put("Authorization", "bearer " + token);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }
}
