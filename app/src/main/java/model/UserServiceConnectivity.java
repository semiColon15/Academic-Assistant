package model;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.android.volley.toolbox.Volley;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserServiceConnectivity {

    //This class handles any requests being made to the Users controller in the Azure service
    //It consists of various volley methods which relates to actions on the Users controller
    //All volley request take a Servercallback object as their first parameter so that the onSuccess/onError methods can be called in other classes

    private Context context;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/";
    private static String TAG = SignUpActivity.class.getSimpleName();

    public UserServiceConnectivity(Context context, ProgressDialog dialog)
    {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        pDialog = dialog;
    }

    public void registerUserWithService(final ServerCallback callback, final User user)
    {
        String url = baseUrl + "Account/Register/?";
        final HashMap<String, String> params = new HashMap<>();
        showpDialog();

        params.put("Email", user.getEmail());
        params.put("Password", user.getPassword());
        params.put("ConfirmPassword", user.getConfirmPassword());

        CustomJsonObjectRequest req = new CustomJsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req);
    }

    public void registerUser(final ServerCallback callback, final User user)
    {
        showpDialog();
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

                        callback.onSuccess(response);
                        hidepDialog();

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

    public void getToken(final ServerCallback callback, final User user)
    {
        showpDialog();
        final String Url2 = "https://academicassistantservice2.azurewebsites.net/token";

        CustomBodyStringRequest req2 = new CustomBodyStringRequest(Url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error is here: ", error.getMessage());
                hidepDialog();
            }
        })
        {
            //to encode the request as x-www-form-urlencoded
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

        req2.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(req2);
    }



    public void retrieveAllUsers(final ServerCallback callback)
    {
        String url = "Users/GetUsers";
        String Url = baseUrl + url;
        showpDialog();
        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

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



    public void checkUserAdminLevel(final ServerCallback callback, final String username)
    {
        String url = "Users/GetUser/?username=" + username;
        String Url = baseUrl + url;
        showpDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
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
}
