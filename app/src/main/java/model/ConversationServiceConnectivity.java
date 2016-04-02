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

    private Context context;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    private String baseUrl = "http://academicassistantservice2.azurewebsites.net/api/";
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
                String token = LogInActivity.token;
                headers.put("Authorization", "bearer "+ token);
                return headers;
            }
        };

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
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context, "Volley error", Toast.LENGTH_LONG).show();
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
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(baseUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                                callback.onSuccess(response);
                            hidepDialog();
                            VolleyLog.v("Response:%n %s", response.toString(4));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ERROR creating group", Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Volley error", Toast.LENGTH_LONG).show();
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
                            hidepDialog();

                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ERRORRR", Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Volley error", Toast.LENGTH_LONG).show();
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void deleteConversation(final ServerCallback callback, String key)
    {
        String url = "Conversations/DeleteConversation?key="+key;
        String Url = baseUrl + url;
        showpDialog();

        StringRequest req = new StringRequest(Request.Method.DELETE, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
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

    public void deleteMessagesInConvo(final ServerCallback callback, int id)
    {
        String url = "Messages/DeleteMessage?id="+id;
        String Url = baseUrl + url;
        showpDialog();

        StringRequest req = new StringRequest(Request.Method.DELETE, Url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
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


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
