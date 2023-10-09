package com.example.csit321.SupportClass;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    //Send notification from admin to other users
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SEVER_KEY = "key=AAAAHsc2k3A:APA91bEWebuyMvTemjdi-3nT2TtUq9k2J6d0lQnSJ1FY63sOIn3dfNGtK8ivsUi_gIvfrb6tZR566lD7zVYhfgk1i-vhvoLSnUN-iFgYrldAD4eMouX1UD6V-uScebAz0Owqz8-6Rkw4";
    private static final String TAG = "FCMSend";

    public static void pushNotification(Context context, String token, String title, String message)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try{
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "FCM "+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    Map <String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SEVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
