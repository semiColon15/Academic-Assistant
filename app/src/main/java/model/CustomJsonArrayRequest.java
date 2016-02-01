package model;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.HashMap;
import java.util.Map;

public class CustomJsonArrayRequest extends JsonArrayRequest
{
    public CustomJsonArrayRequest(String url, Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(url, listener, errorListener);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "bearer CtNSwa_2-6tbdPfrljXpU7GJttxDBvK_Ml3sXbdP1BhLAw_rDy4Phw6qKV9h2qtW14uejMYSMyCPPrExKD-O8ZgBYYlx6CQPuhBQdTj-VTtXE1juGLDWjws2lccNs2jvu9Dndxo9K0SS8LOWG2W6Rc91n5d1PHzycIHeF9ujO_fi3CZkAw7AJLG3y4dSN0o50DtrSOGUl9h5GEG6kCOq3lbq7oZbIszbt50i4eOT9RDQ-kYleNLJsgH5kYsoXnsKX3Xpt45iAh9DltNOdeUXq6OP7aPOs53H1WqOx7xOcPtihQFccqbuqxBf8MdDts5c95xMGTCPiNixS-NRv28qCaRYCRcsxNSdUEZl1oI9ARzSUGpNNASgPb4lZFlwgVEbQnndItRJv3mWMUu5krhfN2fvSrALOC2g1lmzCpQAjmXGWgkc6_H9qdtw76rC2IKEUyPCnEHvYABZcWxEgS7R1SZf-guXe4NXDkI44ZK0gq7sGlCGEhpzttC-VcRi16ql");
        return headers;
    }

}
