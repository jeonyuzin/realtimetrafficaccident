package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_goReg;
    private Button btn_goLogin;
    private Button btn_test;
    private Button btn_test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 최대화
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        btn_goLogin= findViewById(R.id.btn_goLogin);
        btn_goLogin.setOnClickListener(this);

        btn_goReg=findViewById(R.id.btn_goReg);
        btn_goReg.setOnClickListener(this);

        btn_test=findViewById(R.id.btn_test);
        btn_test.setOnClickListener(this);

        btn_test2=findViewById(R.id.btn_test2);
        btn_test2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v==btn_goReg){
            Intent regIntet =new Intent(MainActivity.this, User_reg.class);
            startActivity(regIntet);
        }
        else if(v==btn_goLogin){
            Intent loginIntet =new Intent(MainActivity.this, User_login.class);
            startActivity(loginIntet);
        }
        else if(v==btn_test){
            Intent testIntent =new Intent(MainActivity.this, Current_map.class);
            startActivity(testIntent);
        }
        else if(v==btn_test2){
            Intent test2Intent=new Intent(MainActivity.this,Total_list.class);
            startActivity(test2Intent);
        }

    }

    @Override //정보 삭제
    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.commit();
        System.out.println("쉐어 삭제");
    }

}