package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteActivity extends AppCompatActivity implements View.OnClickListener {

    private int cp_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        Bundle bundle = getIntent().getExtras();
        try {
            JSONObject jsonObject = new JSONObject(bundle.getString("json"));
            cp_id = (int)jsonObject.get("id");
            System.out.println(jsonObject);
            EditText et_name = (EditText) findViewById(R.id.delete_name);
            EditText et_phone = (EditText) findViewById(R.id.delete_phone);

            et_name.setText(jsonObject.get("name").toString());
            et_phone.setText(jsonObject.get("phone").toString());

            ((Button)findViewById(R.id.button_delete)).setOnClickListener(this);
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
               intent.setClass(DeleteActivity.this, MainActivity.class);
               Bundle bundle = new Bundle();
               if(msg.what == 1)
                   bundle.putString("msg","已刪除!");
               else
                   bundle.putString("msg", "刪除失敗!");
               intent.putExtras(bundle);
               startActivity(intent);
               DeleteActivity.this.finish();
            }
        };
        if (bn.getId() == R.id.button_delete)
        {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();

                    PostgresqlAPI pgapi = new PostgresqlAPI();
                    try {
                        if(pgapi.deleteUser(cp_id))
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
            System.out.println("this is confirm cp_id=" + cp_id);
        }else{
            Intent intent = new Intent();
            intent.setClass(DeleteActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}