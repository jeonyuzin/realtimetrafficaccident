package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Total_list extends AppCompatActivity {



    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;
    Date date = new Date();
    ProgressDialog progressDialog = null;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateforamt = new SimpleDateFormat("yyyy-MM-dd");
    String now_date = dateforamt.format(date);
    ArrayList<accident_info> accidents = new ArrayList<>();
    ArrayList<String> times=new ArrayList<>();
    LinearLayout.LayoutParams params=null;
    LinearLayout lm=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_list);

        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(20 * dm.density);
        //버튼 동적 생성
        System.out.println("여백 크기 : " +size);
        lm= findViewById(R.id.base_layout);

        progressDialog=new ProgressDialog(Total_list.this);//다이얼로그선언 activity객체
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);//외부클릭으로 종료 x

        //linearLayout params정의
        params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.leftMargin=size;
        params.rightMargin=size;
        params.bottomMargin=size;

        progressDialog.show();
        readData(new Total_list.FirebaseCallback() {
            @Override
            public void onCallback(String msg) {
                System.out.println("완료");
                getData();
                progressDialog.dismiss();
            }
        });


    }
    private void getData(){
        System.out.println(times.get(0));
        for(int i=0; i<accidents.size(); i++){
            //LinearLayout 생성
            LinearLayout temp_ll=new LinearLayout(this);
            temp_ll.setOrientation(LinearLayout.HORIZONTAL);

            //TextView생성
            TextView lld=new TextView(this);
            lld.setText(times.get(i));
            lld.setLayoutParams(params);
            temp_ll.addView(lld);

            //Textview id 2만부터
            TextView pro=new TextView(this);
            pro.setId(20000+i);
            String color_check=accidents.get(i).getPro();
            pro.setText(accidents.get(i).getPro());
            if (color_check.equals("false")){
                pro.setBackgroundColor(Color.RED);
            }
            else{
                pro.setBackgroundColor(Color.BLUE);
            }
            lld.setLayoutParams(params);
            temp_ll.addView(pro);

            final Button btn_info=new Button(this);
            btn_info.setId(i);
            //버튼 번호:정보 수정할때 필요
            btn_info.setText("정보 보기");
            btn_info.setLayoutParams(params);

            final Button btn_pro=new Button(this);
            //버튼 번호::정보 수정할때 필요 10000을 더해 구분
            btn_pro.setId(10000+i);
            btn_pro.setText("처리 변경");
            btn_pro.setLayoutParams(params);

            btn_info.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Total_list.this, PopupActivity.class);
                    intent.putExtra("imgurl", accidents.get(v.getId()).getImg_str());
                    intent.putExtra("lld",accidents.get(v.getId()).getL_lld().toString());
                    intent.putExtra("phone",accidents.get(v.getId()).getPhone());
                    startActivity(intent);
                }
            });

            btn_pro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updateData(v.getId())){//처리시변경
                        try {
                            System.out.println("새로고침 실행");
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        }
                        catch (Exception e){
                            System.out.println(e);
                        }
                        Toast.makeText(Total_list.this, "처리 성공", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(Total_list.this, "처리 변경 실패", Toast.LENGTH_LONG).show();
                    }
                }
            });
            temp_ll.addView(btn_info);
            temp_ll.addView(btn_pro);
            lm.addView(temp_ll);

        }
//        for(int i=3; i<20; i++){ 슬라이드 구현 확인용
//            //LinearLayout 생성
//            LinearLayout temp_ll=new LinearLayout(this);
//            temp_ll.setOrientation(LinearLayout.HORIZONTAL);
//
//            //TextView생성
//            TextView lld=new TextView(this);
//            lld.setText("더미");
//            lld.setLayoutParams(params);
//            temp_ll.addView(lld);
//
//            TextView pro=new TextView(this);
//            pro.setText("더미");
//            lld.setLayoutParams(params);
//            temp_ll.addView(pro);
//
//            final Button btn_info=new Button(this);
//            btn_info.setId(i);
//            //버튼 번호:정보 수정할때 필요
//            btn_info.setText("정보 보기");
//            btn_info.setLayoutParams(params);
//
//            final Button btn_pro=new Button(this);
//            //버튼 번호::정보 수정할때 필요 10000을 더해 구분
//            btn_pro.setId(10000+i);
//            btn_pro.setText("처리 변경");
//            btn_pro.setLayoutParams(params);
//
//            temp_ll.addView(btn_info);
//            temp_ll.addView(btn_pro);
//            lm.addView(temp_ll);
//
//        }
    }

    private void readData(Total_list.FirebaseCallback firebaseCallback){
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accidents.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.child(now_date).getChildren()) {
                    try {
                        ArrayList<String> value = (ArrayList<String>) postSnapshot.getValue();
                        times.add(postSnapshot.getKey());
                        accidents.add(new accident_info(value.get(0),value.get(1),value.get(2), value.get(3)));
                    }
                    catch(Exception e){
                        System.out.println("에러 : "+e );
                    }
                }
                firebaseCallback.onCallback("완료");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    private interface FirebaseCallback{
        void onCallback(String msg);
    }
    private boolean updateData(int index){
        System.out.println("업데이트실행");
        SharedPreferences sharedPreferences= getSharedPreferences("user_info", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        String user_phone = sharedPreferences.getString("user_phone","");
        System.out.println(user_phone);
        int s_index=index-10000;
        DatabaseReference rupdate=myDB_Reference.child(now_date).child(times.get(s_index));
        Map<String, Object> acc_Updates = new HashMap<>();
        if(accidents.get(s_index).getPro().equals("false")){
            acc_Updates.put("1","true");
            acc_Updates.put("2",user_phone);
        }
        else{
            acc_Updates.put("1","false");
            acc_Updates.put("2",user_phone);
        }
        try{
            rupdate.updateChildren(acc_Updates);
        }
        catch (Exception e){
            System.out.println("데이터 업데이트 에러");
            return false;
        }
        return true;
    }
}