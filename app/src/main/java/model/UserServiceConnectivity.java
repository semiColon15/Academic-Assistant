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
import com.hooper.kenneth.academicassistant.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserServiceConnectivity {

    //private ProgressDialog pDialog;
    private Context context;
    private RequestQueue mRequestQueue;

    String baseUrl = "https://academicassistant20151209121006.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    private String[] allEmails;
    private String[] allPasswords;

    private String token;

    public UserServiceConnectivity(Context context)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        retrieveAllUserEmailAddresses();
        retrieveAllPasswords();
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

    public void registerUser(final User user)
    {
        String url = baseUrl + "Users/PostUser/?";
        HashMap<String, String> params = new HashMap<>();
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
                            getToken(user);

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

    public void getToken(final User user)
    {
        final String Url2 = "https://academicassistant20151209121006.azurewebsites.net/Token";

        CustomBodyStringRequest req2 = new CustomBodyStringRequest(Url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            token = jsonResponse.getString("access_token");
                            System.out.println("TOKEN: " + token);

                            VolleyLog.v("Response:%n %s", response);
                            Toast.makeText(context, "Account Registered. Token Recieved", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
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
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<>();
                pars.put("grant_type", "password");
                pars.put("userName", user.getEmail());
                pars.put("password", user.getPassword());
                return pars;
            }
    };

        mRequestQueue.add(req2);
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

    public void retrieveAllPasswords()
    {
        String url = "Users/GetUsers";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            allPasswords = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject passwords = (JSONObject) response.get(i);

                                allPasswords[i] = passwords.getString("Password");
                                System.out.println("UP:  " + allPasswords[i]);
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

    public String[] getAllPasswords()
    {
        return allPasswords;
    }

    public void logIn()
    {

    }
}
