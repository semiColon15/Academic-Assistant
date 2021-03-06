package model;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConversationServiceConnectivity {

    //This class handles any requests being made to the Conversations controller in the Azure service
    //It consists of various volley methods which relates to actions on the Conversations controller
    //All volley request take a Servercallback object as their first parameter so that the onSuccess/onError methods can be called in other classes

    private Context context;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    private String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    public ConversationServiceConnectivity(Context context, ProgressDialog pDialog)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        this.pDialog = pDialog;
    }

    public void getExistingConversations(final ServerCallback callback)
    {
        String url = "Conversations/GetConversations";
        String Url = baseUrl + url;
        showpDialog();

        //JsonArrayRequest to handle a list of JsonObjects
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
                hidepDialog();
            }
        }){
            //Send access token with request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                String token = LogInActivity.token;
                headers.put("Authorization", "bearer "+ token);
                return headers;
            }
        };

        //set retry if request times out
        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void getConversation(final ServerCallback callback, String key)
    {
        String url = "Conversations/GetConversation?key="+key;
        String Url = baseUrl + url;
        showpDialog();
        pDialog.setMessage("Gathering Information...");

        JsonObjectRequest req = new JsonObjectRequest(Url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void getConversationsForUser(final ServerCallback callback, String email)
    {
        String url = "Conversations/GetConversationForUser?email="+email;
        String Url = baseUrl + url;
        pDialog.setMessage("Loading Conversations...");
        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        callback.onSuccess(response);
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void CreateConversation(final ServerCallback callback, String key, String name, String admin)
    {
        String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/Conversations/PostConversation?";
        HashMap<String, String> params = new HashMap<>();
        params.put("Key", key);
        params.put("ConversationName", name);
        params.put("Administrator", admin);
        pDialog.setMessage("Creating Converstion..");
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                                callback.onSuccess(response);
                            VolleyLog.v("Response:%n %s", response.toString(4));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error creating group..", Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void AddUserIntoConversation(final ServerCallback callback, String key, String email, String password, String admin)
    {
        String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/Conversations/PostAddUser?key=" + key;
        HashMap<String, String> params = new HashMap<>();
        params.put("Email", email);
        params.put("Password", password);
        params.put("Admin", admin);
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.onSuccess(response);

                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
                VolleyLog.e("Error: ", error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer " + LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void deleteConversation(final ServerCallback callback, String key)
    {
        String url = "Conversations/DeleteConversation?key="+key;
        String Url = baseUrl + url;
        pDialog.setMessage("Deleting Conversation...");
        showpDialog();

        StringRequest req = new StringRequest(Request.Method.DELETE, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void deleteMessagesInConvo(final ServerCallback callback, int id)
    {
        String url = "Messages/DeleteMessage?id="+id;
        String Url = baseUrl + url;
        showpDialog();

        StringRequest req = new StringRequest(Request.Method.DELETE, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void RemoveUserFromGroup(final ServerCallback callback, String key, User user)
    {
        String url = "Conversations/RemoveUserFromGroup?key="+key;
        String Url = baseUrl + url;
        HashMap<String, String> params = new HashMap<>();
        params.put("Email", user.getEmail());
        params.put("Password", user.getPassword());
        params.put("Admin", Boolean.toString(user.getAdminUser()));
        pDialog.setMessage("Leaving Group...");
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, Url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidepDialog();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<>();
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
}
