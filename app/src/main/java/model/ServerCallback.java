package model;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ServerCallback {
    void onSuccess(JSONObject result);
    void onSuccess(JSONArray result);
    void onSuccess(String result);
    void onError(VolleyError error);
}
