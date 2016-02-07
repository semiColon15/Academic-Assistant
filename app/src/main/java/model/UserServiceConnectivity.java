package model;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.SignUpActivity;
import com.hooper.kenneth.academicassistant.ViewMessagesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class UserServiceConnectivity {

    private ProgressDialog pDialog;
    private Context context;
    private RequestQueue mRequestQueue;

    String baseUrl = "https://academicassistant20151209121006.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    private String[] allEmails;

    private String token;

    public UserServiceConnectivity(Context context)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        retrieveAllUserEmailAddresses();
    }

    public void registerUserWithService(final User user)
    {
        String url = baseUrl + "Account/Register/?";
        final HashMap<String, String> params = new HashMap<>();


        params.put("Password", user.getPassword());
        System.out.println(params);
        params.put("Email", user.getEmail());
        System.out.println(params);
        params.put("ConfirmPassword", user.getConfirmPassword());
        System.out.println(params);

        JSONObject n = new JSONObject(params);

        System.out.println("THIS IS WHAT PARAMS LOOKS LIKE 1:  " + n);

        CustomJsonObjectRequest req = new CustomJsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("Loop 0: ");
                            //VolleyLog.v("Response:%n %s", response.toString(4));
                            System.out.println("Loop 1: ");
                            registerUser(user);
                            System.out.println("Loop 2: ");

                            Toast.makeText(context, "Added Acount", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Loop 3: ");
                            Toast.makeText(context, "Account Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error is here 1: ", error.getMessage());
                System.out.println(error.toString());
                System.out.println("Loop 4: ");
                System.out.println("PARAMS: " + params);
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(req);
    }

    public void registerUser(User user)
    {
        String url = baseUrl + "Users/PostUser/?";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Email", user.getEmail());
        params.put("Password", user.getPassword());
        params.put("Admin", String.valueOf(user.getAdminUser()));

        System.out.println("PARAMS: " + params);
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Added Acount to users", Toast.LENGTH_LONG).show();
                            System.out.println("Loop 5: ");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Loop 6: ");
                            Toast.makeText(context, "Account Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {VolleyLog.e("Error is here 2: ", error.getMessage());
                    System.out.println(error.toString());
                    System.out.println("Loop 7: ");
                    Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
                }
            });

        mRequestQueue.add(req);
    }

    public String getToken(final User user)
    {
        final String Url2 = "http://academicassistant20151209121006.azurewebsites.net/Token";
        final HashMap<String, String> params2 = new HashMap<String, String>();
        params2.put("grant_type", "password");
        params2.put("userName", user.getEmail());
        params2.put("password", user.getPassword());

        JsonObjectRequest req2 = new JsonObjectRequest(Url2, new JSONObject(params2),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String key = response.toString();
                            token = key;
                            System.out.println("RESPONSE " + token);
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Toast.makeText(context, "Account Registered. Token Recieved", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Account Registration Error", Toast.LENGTH_LONG).show();
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
        mRequestQueue.add(req2);
        return token;
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
                            //Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
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
