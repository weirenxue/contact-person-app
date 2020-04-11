package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 1:
                        try {
                            int num = 0;
                            LinearLayout container = (LinearLayout) findViewById(R.id.container);
                            JSONObject jsonObject = (JSONObject)msg.obj;
                            System.out.println("in handler "+ jsonObject.getJSONArray("records").length());
                            num = jsonObject.getJSONArray("records").length();
                            for (int i = 0; i < num; i++) {
                                //新增聯絡人的按扭
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
                                contact.addView(bn_update);

                                //刪除的按扭
                                Button bn_delete = new Button(MainActivity.this);
                                bn_delete.setText(R.string.button_delete);
                                bn_delete.setGravity(Gravity.CENTER);
                                contact.addView(bn_delete);
                                bn_delete.setOnClickListener(MainActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        new Thread(new Runnable(){
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
                    /*num = jsonObject.getJSONArray("records").length();
                    for (int i = 0; i < num; i++) {
                        //新增聯絡人的按扭
                        Button add_user = (Button) findViewById(R.id.add_contact);
                        add_user.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                        //新LinearLayout,每個聯絡人一個
                        LinearLayout contact = new LinearLayout(getApplicationContext());
                        contact.setOrientation(LinearLayout.HORIZONTAL);
                        contact.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        container.addView(contact);

                        //放名字的TextView
                        TextView tv_name = new TextView(getApplicationContext());
                        tv_name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        tv_name.setText(jsonObject.getJSONArray("records").getJSONObject(i).get("name").toString());
                        tv_name.setTextSize(20);
                        tv_name.setGravity(Gravity.LEFT);
                        contact.addView(tv_name);

                        //放手機的TextView
                        TextView tv_phone = new TextView(getApplicationContext());
                        tv_phone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                        tv_phone.setText(jsonObject.getJSONArray("records").getJSONObject(i).get("phone").toString());
                        tv_phone.setTextSize(20);
                        tv_phone.setGravity(Gravity.CENTER);
                        contact.addView(tv_phone);

                        //更新的按扭
                        Button bn_update = new Button();
                        bn_update.setText(R.string.button_update);
                        //bn_update.setOnClickListener(getApplicationContext());
                        contact.addView(bn_update);

                        //刪除的按扭
                        /*Button bn_delete = new Button(getApplicationContext());
                        bn_delete.setText(R.string.button_delete);
                        bn_delete.setGravity(Gravity.CENTER);
                        contact.addView(bn_delete);
                        //bn_delete.setOnClickListener(getApplicationContext());
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    public void onClick(View v) {
        ((Button) v).setText("");
    }

}