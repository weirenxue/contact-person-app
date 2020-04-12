package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private int cp_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Bundle bundle = getIntent().getExtras();
        try {
            JSONObject jsonObject = new JSONObject(bundle.getString("json"));
            cp_id = (int)jsonObject.get("id");
            System.out.println(jsonObject);
            EditText et_name = (EditText) findViewById(R.id.update_name);
            EditText et_phone = (EditText) findViewById(R.id.update_phone);

            et_name.setText(jsonObject.get("name").toString());
            et_phone.setText(jsonObject.get("phone").toString());

            ((Button)findViewById(R.id.button_update)).setOnClickListener(this);
            ((Button)findViewById(R.id.button_cancel)).setOnClickListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onClick(View v){
        Button bn = (Button) v;
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Intent intent = new Intent();
                intent.setClass(UpdateActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                if(msg.what == 1)
                    bundle.putString("msg","已更新!");
                else
                    bundle.putString("msg", "更新失敗!");
                intent.putExtras(bundle);
                startActivity(intent);
                UpdateActivity.this.finish();
            }
        };
        if (bn.getId() == R.id.button_update)
        {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();

                    PostgresqlAPI pgapi = new PostgresqlAPI();
                    EditText et_name = (EditText) findViewById(R.id.update_name);
                    EditText et_phone = (EditText) findViewById(R.id.update_phone);
                    HashMap map = new HashMap();
                    map.put("id", cp_id);
                    map.put("name", et_name.getText().toString());
                    map.put("phone", et_phone.getText().toString());
                    JSONObject jsonObject = new JSONObject(map);
                    System.out.println(jsonObject);
                    try {
                        if(pgapi.updateUser(jsonObject))
                            msg.what = 1;
                        else
                            msg.what = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(msg);
                }
            });
            th.start();
            System.out.println("this is confirm cp_id=" + cp_id);
        }else{
            Intent intent = new Intent();
            intent.setClass(UpdateActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}