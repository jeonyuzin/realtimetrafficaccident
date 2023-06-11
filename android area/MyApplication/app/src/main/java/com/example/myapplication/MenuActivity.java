package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_map=null;
    Button btn_total=null;
    Button btn_back3=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btn_map=findViewById(R.id.btn_map);
        btn_total =findViewById(R.id.btn_total);
        btn_back3=findViewById(R.id.btn_back3);

        btn_map.setOnClickListener(this::onClick);
        btn_total.setOnClickListener(this::onClick);
        btn_back3.setOnClickListener(this::onClick);

    }


    @Override//로그아웃시 정보 삭제
    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.commit();
        System.out.println("쉐어 삭제");
    }

    @Override
    public void onClick(View v) {
        if(v==btn_map){
            Intent mapIntet =new Intent(MenuActivity.this, Current_map.class);
            startActivity(mapIntet);
        }
        else if(v==btn_total){
            Intent totalIntet =new Intent(MenuActivity.this, Total_list.class);
            startActivity(totalIntet);
        }
        else if(v==btn_back3){
            finish();
        }
    }
}