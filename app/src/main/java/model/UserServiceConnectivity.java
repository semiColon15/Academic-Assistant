package model;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.SignUpActivity;
import com.hooper.kenneth.academicassistant.ViewMessagesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserServiceConnectivity {

    private ProgressDialog pDialog;
    private Context context;
    private RequestQueue mRequestQueue;

    String baseUrl = "http://academicassistant20151209121006.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    private String[] allEmails;

    public UserServiceConnectivity(Context context)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        retrieveAllUserEmailAddresses();
    }

    public void registerUser(User user)
    {
        String url = baseUrl + "Users/PostUser/?";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Email", user.getEmail());
        params.put("Password", user.getPassword());
        params.put("ConfirmPassword", user.getConfirmPassword());

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Added Acount", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Account Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error is here: ", error.getMessage());
                System.out.println(error.toString());
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(req);
    }

    public void retrieveAllUserEmailAddresses()
    {
        String url = "Users/GetUsers";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            allEmails = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject emails = (JSONObject) response.get(i);

                                allEmails[i] = emails.getString("Email");
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
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        mRequestQueue.add(req);
    }

    public String[] getAllEmailAddresses()
    {
        return allEmails;
    }
}
