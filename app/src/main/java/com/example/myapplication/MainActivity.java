package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && bundle.containsKey("msg")) {
            Toast.makeText(this, bundle.getString("msg"), Toast.LENGTH_LONG).show();
            System.out.println("yes!");
        }

        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 1:
                        try {
                            int num = 0;
                            int id = 0;
                            LinearLayout container = (LinearLayout) findViewById(R.id.container);
                            JSONObject jsonObject = (JSONObject)msg.obj;
                            System.out.println("in handler "+ jsonObject.getJSONArray("records").length());
                            num = jsonObject.getJSONArray("records").length();
                            for (int i = 0; i < num; i++) {
                                //新增聯絡人的按扭
                                id = (int)jsonObject.getJSONArray("records").getJSONObject(i).get("id");
                                System.out.println();

                                Button add_user = (Button) findViewById(R.id.add_contact);
                                add_user.setOnClickListener(MainActivity.this);
                                //新LinearLayout,每個聯絡人一個
                                LinearLayout contact = new LinearLayout(MainActivity.this);
                                contact.setOrientation(LinearLayout.HORIZONTAL);
                                contact.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                container.addView(contact);

                                //放名字的TextView
                                TextView tv_name = new TextView(MainActivity.this);
                                tv_name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                tv_name.setText(jsonObject.getJSONArray("records").getJSONObject(i).get("name").toString());
                                tv_name.setTextSize(20);
                                tv_name.setGravity(Gravity.LEFT);
                                contact.addView(tv_name);

                                //放手機的TextView
                                TextView tv_phone = new TextView(MainActivity.this);
                                tv_phone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                                tv_phone.setText(jsonObject.getJSONArray("records").getJSONObject(i).get("phone").toString());
                                tv_phone.setTextSize(20);
                                tv_phone.setGravity(Gravity.CENTER);
                                contact.addView(tv_phone);

                                //更新的按扭
                                Button bn_update = new Button(MainActivity.this);
                                bn_update.setText(R.string.button_update);
                                bn_update.setOnClickListener(MainActivity.this);
                                bn_update.setId(id);
                                contact.addView(bn_update);

                                //刪除的按扭
                                Button bn_delete = new Button(MainActivity.this);
                                bn_delete.setText(R.string.button_delete);
                                bn_delete.setGravity(Gravity.CENTER);
                                contact.addView(bn_delete);
                                bn_delete.setId(id);
                                bn_delete.setOnClickListener(MainActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                Message msg = new Message();
                PostgresqlAPI pgapi = new PostgresqlAPI();
                int num = 0;
                JSONObject jsonObject = null;

                try {
                    jsonObject = pgapi.queryUser();
                    System.out.println("The number of users：" + jsonObject.getJSONArray("records").length());
                    msg.obj = (Object)jsonObject;
                    // query
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();

    }
    @Override
    public void onClick(View v) {
        Button bn = ((Button) v);
        final int bn_id = bn.getId();
        String bn_text = bn.getText().toString();
        String deleteText = getResources().getString(R.string.button_delete);
        String updateText = getResources().getString(R.string.button_update);
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                JSONObject jsonObject = (JSONObject) msg.obj;
                switch(msg.what) {
                    // delete
                    case 1:
                        Intent deleteIntent = new Intent();
                        deleteIntent.setClass(MainActivity.this  , DeleteActivity.class);
                        Bundle deleteBundle = new Bundle();
                        deleteBundle.putString("json", jsonObject.toString());
                        deleteIntent.putExtras(deleteBundle);
                        startActivity(deleteIntent);
                        MainActivity.this.finish();
                        break;
                    //update
                    case 2:
                        Intent updateIntent = new Intent();
                        updateIntent.setClass(MainActivity.this, UpdateActivity.class);
                        Bundle updateBundle = new Bundle();
                        updateBundle.putString("json", jsonObject.toString());
                        updateIntent.putExtras(updateBundle);
                        startActivity(updateIntent);
                        MainActivity.this.finish();
                        break;
                    //insert
                    case 3:
                        Intent addoneIntent = new Intent();
                        addoneIntent.setClass(MainActivity.this, AddOneActivity.class);
                        startActivity(addoneIntent);
                        MainActivity.this.finish();
                        break;
                }
            }
        };

        if (bn_id == R.id.add_contact){
            System.out.println("this is ADD " );
            Thread th = new Thread(new TransactionRun(3, mHandler, bn_id));
            th.start();
        } else if (bn_text.equals(deleteText))
        {
            Thread th = new Thread(new TransactionRun(1, mHandler, bn_id));
            th.start();
            System.out.println("this is DELETE " + bn_id);

        } else if (bn_text.equals(updateText))
        {
            Thread th = new Thread(new TransactionRun(2, mHandler, bn_id));
            th.start();
            System.out.println("this is UPDATE " + bn_id);
        }

    }

}
