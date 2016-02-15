package model;

import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

public class CustomBodyStringRequest extends StringRequest {

    public CustomBodyStringRequest(String url, Response.Listener<String> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
    }
}