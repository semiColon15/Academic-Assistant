package model;

import android.net.sip.SipAudioCall;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomJsonObjectRequest extends JsonObjectRequest
{
    private String token;

    public CustomJsonObjectRequest(String url, JSONObject jsonRequest,Response.Listener listener, Response.ErrorListener errorListener, String token)
    {
        super(url, jsonRequest, listener, errorListener);
        this.token = token;
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("Authorization", "bearer " + token);
        return headers;
    }

}
