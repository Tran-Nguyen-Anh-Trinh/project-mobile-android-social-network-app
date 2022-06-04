package com.midterm.cloneinstagram.PushNotify;

import android.content.Context;
import android.os.StrictMode;

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
    static RequestQueue queue;
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAiBTDrMo:APA91bHDOdH6Tohu7PuYRg2aCBRq1E_LT4L5nRdNQn-VgWh9WWjsxMKJOOBf5iQHsOzj7ookGo0ara1ZYbpZ0RjbxpqMbcAahZ9RYC1sRfjjnZ684OwzfxCuPyHkm_yMHPeXccL0Wj74";

    public static void pushNotification(Context context, String token, String title, String messages, String idSend, String nameSend, String avaSend, String Uid, String Name, String ReceiverImg){
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", token);
//            JSONObject notification = new JSONObject();
//            notification.put("title", title);
//            notification.put("body", messages);
//            json.put("notification", notification);
            JSONObject jsonData = new JSONObject();
            jsonData.put("title", title);
            jsonData.put("body", messages);
            jsonData.put("idSend", idSend);
            jsonData.put("nameSend", nameSend);
            jsonData.put("avaSend", avaSend);
            jsonData.put("Uid", Uid);
            jsonData.put("Name", Name);
            jsonData.put("ReceiverImg", ReceiverImg);
            json.put("data", jsonData);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM"+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);


        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
