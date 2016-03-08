package model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        getExistingConversations();
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
                    String token = LogInActivity.token; //"CtNSwa_2-6tbdPfrljXpU7GJttxDBvK_Ml3sXbdP1BhLAw_rDy4Phw6qKV9h2qtW14uejMYSMyCPPrExKD-O8ZgBYYlx6CQPuhBQdTj-VTtXE1juGLDWjws2lccNs2jvu9Dndxo9K0SS8LOWG2W6Rc91n5d1PHzycIHeF9ujO_fi3CZkAw7AJLG3y4dSN0o50DtrSOGUl9h5GEG6kCOq3lbq7oZbIszbt50i4eOT9RDQ-kYleNLJsgH5kYsoXnsKX3Xpt45iAh9DltNOdeUXq6OP7aPOs53H1WqOx7xOcPtihQFccqbuqxBf8MdDts5c95xMGTCPiNixS-NRv28qCaRYCRcsxNSdUEZl1oI9ARzSUGpNNASgPb4lZFlwgVEbQnndItRJv3mWMUu5krhfN2fvSrALOC2g1lmzCpQAjmXGWgkc6_H9qdtw76rC2IKEUyPCnEHvYABZcWxEgS7R1SZf-guXe4NXDkI44ZK0gq7sGlCGEhpzttC-VcRi16ql";
                    headers.put("Authorization", "bearer "+ token);
                    System.out.println("************* " + headers);
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


    public void getExistingConversations()
    {
        String url = "Conversations/GetConversations";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            allConversations = new ArrayList<>();
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
                                allConversations.add(con);

                                System.out.print("CONVERSATIONS " + con.getKey() + ", " + con.getConversationName());

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
                String token = LogInActivity.token; //"CtNSwa_2-6tbdPfrljXpU7GJttxDBvK_Ml3sXbdP1BhLAw_rDy4Phw6qKV9h2qtW14uejMYSMyCPPrExKD-O8ZgBYYlx6CQPuhBQdTj-VTtXE1juGLDWjws2lccNs2jvu9Dndxo9K0SS8LOWG2W6Rc91n5d1PHzycIHeF9ujO_fi3CZkAw7AJLG3y4dSN0o50DtrSOGUl9h5GEG6kCOq3lbq7oZbIszbt50i4eOT9RDQ-kYleNLJsgH5kYsoXnsKX3Xpt45iAh9DltNOdeUXq6OP7aPOs53H1WqOx7xOcPtihQFccqbuqxBf8MdDts5c95xMGTCPiNixS-NRv28qCaRYCRcsxNSdUEZl1oI9ARzSUGpNNASgPb4lZFlwgVEbQnndItRJv3mWMUu5krhfN2fvSrALOC2g1lmzCpQAjmXGWgkc6_H9qdtw76rC2IKEUyPCnEHvYABZcWxEgS7R1SZf-guXe4NXDkI44ZK0gq7sGlCGEhpzttC-VcRi16ql";
                headers.put("Authorization", "bearer "+ token);
                System.out.println("************* " + headers);
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
                String token = LogInActivity.token; //"CtNSwa_2-6tbdPfrljXpU7GJttxDBvK_Ml3sXbdP1BhLAw_rDy4Phw6qKV9h2qtW14uejMYSMyCPPrExKD-O8ZgBYYlx6CQPuhBQdTj-VTtXE1juGLDWjws2lccNs2jvu9Dndxo9K0SS8LOWG2W6Rc91n5d1PHzycIHeF9ujO_fi3CZkAw7AJLG3y4dSN0o50DtrSOGUl9h5GEG6kCOq3lbq7oZbIszbt50i4eOT9RDQ-kYleNLJsgH5kYsoXnsKX3Xpt45iAh9DltNOdeUXq6OP7aPOs53H1WqOx7xOcPtihQFccqbuqxBf8MdDts5c95xMGTCPiNixS-NRv28qCaRYCRcsxNSdUEZl1oI9ARzSUGpNNASgPb4lZFlwgVEbQnndItRJv3mWMUu5krhfN2fvSrALOC2g1lmzCpQAjmXGWgkc6_H9qdtw76rC2IKEUyPCnEHvYABZcWxEgS7R1SZf-guXe4NXDkI44ZK0gq7sGlCGEhpzttC-VcRi16ql";
                headers.put("Authorization", "bearer "+ token);
                System.out.println("************* " + headers);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }


    public ArrayList<Conversation> getConversationsForUser(String email)
    {
        ArrayList<Conversation> convos = new ArrayList<>();
        for(int i=0;i<retrieveAllConversations().size();i++)
        {
            for(int j=0;j<retrieveAllConversations().get(i).getMembers().size();j++)
            {
                if (retrieveAllConversations().get(i).getMembers().get(j).getEmail().equalsIgnoreCase(email) || retrieveAllConversations().get(i).getAdministrator().equalsIgnoreCase(email))
                {
                    if(convos.contains(retrieveAllConversations().get(i)))
                    {
                    }
                    else {
                        convos.add(retrieveAllConversations().get(i));
                    }
                }
            }
        }
        return convos;
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
                headers.put("Authorization", "bearer "+ token);
                System.out.println("************* " + headers);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }
}
