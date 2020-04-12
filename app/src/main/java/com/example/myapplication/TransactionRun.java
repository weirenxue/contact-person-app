package com.example.myapplication;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;


public class TransactionRun implements Runnable{
    private int action; //delete for 1, update for 2, add for 3.
    private Handler mHandler;
    private int bn_id;
    public TransactionRun(int action, Handler mHandler, int bn_id){
        this.action = action;
        this.mHandler = mHandler;
        this.bn_id = bn_id;
    }
    @Override
    public void run(){
        Message msg = new Message();
        PostgresqlAPI pgapi = new PostgresqlAPI();
        int num = 0;
        JSONObject jsonObject = null;
        if (action != 3)
        {
            try {
                jsonObject = pgapi.queryOneUser(bn_id);
                System.out.println("The number of users：" + jsonObject);
                msg.obj = (Object)jsonObject;
                // query
                msg.what = action;
                this.mHandler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            msg.what = action;
            this.mHandler.sendMessage(msg);
        }
    }
}
