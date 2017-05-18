package com.bytesandgears.iotapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText ipEditText;
    private ListView listView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL_PATH = "";
    private ArrayList<ArrayList<String>> items;
    private OutputCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        ipEditText = (EditText) findViewById(R.id.ip);
        listView = (ListView) findViewById(R.id.listView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ipEditText.getText().toString().equals("")) {
                    init();
                } else
                    Toast.makeText(getApplicationContext(),
                            "Por favor inserte una dirección IP",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        Request request = new Request.Builder().url("http://" + ipEditText.getText().toString() + '/').build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No hay conexión con el dispositivo",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (response.isSuccessful()) {

                    Log.d(TAG, json);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setList(json);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Bad Request",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void setList(String data) throws JSONException {
        JSONArray outputs = new JSONArray(data);
        items = new ArrayList<ArrayList<String>>();

        for(int i = 0; i < outputs.length(); ++i) {
            ArrayList<String> row = new ArrayList<String>();
            row.add(outputs.getJSONObject(i).getInt("output") + "");
            row.add(outputs.getJSONObject(i).getInt("status") + "");

            items.add(row);
        }

        adapter = new OutputCustomAdapter(this, items);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).get(1).equals("1"))
                    turn("OFF", id, position);
                else
                    turn("ON", id, position);
            }
        });
    }

    private void turn(final String action, long output, final int position){

        Request request = new Request.Builder().url("http://" + ipEditText.getText().toString()
                + "/" + output + "=" + action).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No hay conexión con el dispositivo",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (response.isSuccessful()) {

                    Log.d(TAG, json);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (action.equals("ON"))
                                items.get(position).set(1, "1");
                            else
                                items.get(position).set(1, "0");

                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Bad Request",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
