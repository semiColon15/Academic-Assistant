package model;

import java.util.Map;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;
    private Context context;

    private static String TAG = ViewMessagesActivity.class.getSimpleName();
    final String baseUrl = "https://academicassistantservice2.azurewebsites.net/api/";

    public MessageServiceConnectivity(Context c, ProgressDialog pDialog)
    {
        context = c;
        this.pDialog = pDialog;
        mRequestQueue = Volley.newRequestQueue(c);
    }


    public void setInitialViews(final ServerCallback callback)
    {
        //IF TIME FIX THIS TO RETURN MESSAGES ONLY IN A SPECIFIED GROUP IN SERVICE
        String url = "Messages/GetMessages";
        showpDialog();
        String Url = baseUrl + url;


        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        callback.onSuccess(response);
                        Log.d(TAG, response.toString());
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

    public void setViews(final ServerCallback callback)
    {
        String url = "Messages/GetMessages";
        String Url = baseUrl + url;

        JsonArrayRequest req = new JsonArrayRequest(Url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        callback.onSuccess(response);
                        Log.d(TAG, response.toString());

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

    public void SendMessage(final ServerCallback callback, String content, String recipient, String sender, String convoKey)
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
                            callback.onSuccess(response);
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
                headers.put("Authorization", "bearer "+ LogInActivity.token);
                return headers;
            }
        };

        mRequestQueue.add(req);
    }

    public void deleteMessage(final ServerCallback callback, int id)
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
}