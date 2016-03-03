package com.hitech.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String PHONE = "1234567890";
    private final String INTERNATIONAL_CODE = "028";
    private final String SERVER_URL = "http://bebetrack.com/api/create";
    private final String PREF_NAME = "API_TEST";
    private final String PREF_TOKEN = "API_TOKEN";

    TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_result = (TextView)findViewById(R.id.tv_result);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData();
            }
        });
    }

    private HttpRequest.HttpRequestListener makeListener(final HttpCommunication.HttpCommListener listener) {
        return new HttpRequest.HttpRequestListener() {
            @Override
            public Object onHttpReceivedData(byte[] data, Object tag) {
                return data != null ? new String(data) : null;
            }

            @Override
            public void onHttpComplete(int networkStatus, Object data, Object tag) {
                listener.onNetworkResult(data != null, (String) data);
            }
        };
    }

    private void SendData() {
        String params = "{\"phone\":\"" + PHONE + "\", \"internationalCode\":\"" + INTERNATIONAL_CODE + "\"}";

        HttpCommunication.sendRequest(new HttpRequest(SERVER_URL, params, makeListener(new HttpCommunication.HttpCommListener() {
            @Override
            public void onNetworkResult(boolean success, String data) {
                if (success) {
                    try {
                        JSONObject json = new JSONObject(data);
                        SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(PREF_TOKEN, json.optString("token"));
                        editor.commit();

                        tv_result.setText("PIN: " + json.optString("pin"));
                    } catch (JSONException e) {
                        Log.e("Test", "JSON Get Response Exception", e);
                    }
                } else {
                    Log.e("Test", "False");
                }
            }
        }), null));
    }
}
