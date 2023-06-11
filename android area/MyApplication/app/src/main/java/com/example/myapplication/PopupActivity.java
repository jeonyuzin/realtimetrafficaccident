package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.naver.maps.geometry.LatLng;

import org.w3c.dom.Text;

public class PopupActivity extends Activity {
    //Activity를 상속받아야 dialog theme runtime에러안남
    ImageView imageView=null;
    TextView text_info=null;
    Button btn_finish=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);


        //UI 객체생성
        imageView = (ImageView) findViewById(R.id.imgView);
        text_info=(TextView)findViewById(R.id.txtinfo);
        btn_finish=(Button)findViewById(R.id.btn_finish);

        //데이터 가져오기
        Intent intent = getIntent();
        String imgurl = intent.getStringExtra("imgurl");
        String phone = intent.getStringExtra("phone");
        String lld=intent.getStringExtra("lld");
        text_info.setText("위도,경도 : "+lld +"\n"+
                "처리자 연락망"+phone);
        //이미지연결 Glide라이브러리  
        //이거아니면 Thread로 예외처리하면서 이미지 스트림
        Glide.with(this).load(imgurl).into(imageView);
        System.out.println(imgurl);

    }

    public void mOnClose(View v){

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}