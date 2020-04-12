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

public class AddOneActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_one);


        ((Button)findViewById(R.id.button_addone)).setOnClickListener(this);
        ((Button)findViewById(R.id.button_cancel)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button bn = (Button) v;
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Intent intent = new Intent();
                intent.setClass(AddOneActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                if(msg.what == 1)
                    bundle.putString("msg","已新增!");
                else
                    bundle.putString("msg", "新增失敗!");
                intent.putExtras(bundle);
                startActivity(intent);
                AddOneActivity.this.finish();
            }
        };
        if (bn.getId() == R.id.button_addone)
        {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    PostgresqlAPI pgapi = new PostgresqlAPI();
                    EditText et_name = (EditText) findViewById(R.id.addone_name);
                    EditText et_phone = (EditText) findViewById(R.id.addone_phone);
                    HashMap map = new HashMap();
                    map.put("name", et_name.getText().toString());
                    map.put("phone", et_phone.getText().toString());
                    JSONObject jsonObject = new JSONObject(map);
                    try {
                        if(pgapi.insertUser(jsonObject))
                            msg.what = 1;
                        else
                            msg.what = 0;
                        mHandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
        }else{
            Intent intent = new Intent();
            intent.setClass(AddOneActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}