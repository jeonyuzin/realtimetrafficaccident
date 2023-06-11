package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Current_map extends FragmentActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private NaverMap mMap;
    private LatLng Current_location = null;
    Date date = new Date();
    Marker marker = new Marker();
    boolean check_loading = false;
    int count = 0;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateforamt = new SimpleDateFormat("yyyy-MM-dd");
    String now_date = dateforamt.format(date);
    ArrayList<accident_info> accidents = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    FirebaseDatabase myFirebase;
    DatabaseReference myDB_Reference = null;
    String info_text = null;
    InfoWindow infoWindow = null;
    ProgressDialog progressDialog = null;
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    String locationProvider;//위치에 따라서 업데이트

    Overlay.OnClickListener listener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("4rivk4rxa8")
        );
        setContentView(R.layout.activity_current_map);
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.nmap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.nmap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        myFirebase = FirebaseDatabase.getInstance();
        myDB_Reference = myFirebase.getReference();

        progressDialog=new ProgressDialog(Current_map.this);//다이얼로그선언 activity객체
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);//외부클릭으로 종료 x
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        System.out.println("onMapReady시작");

        mMap = naverMap;
        long minTime = 1;
        float minDistance = 5;

        mMap.setMapType(NaverMap.MapType.Navi);
        mMap.setSymbolScale(1.0f);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }//위치가 변할때

            public void onStatusChanged(String provider, int status, Bundle extras) {
                mAlertStatus(provider);
            }

            public void onProviderEnabled(String provider) {
                mAlertProvider(provider);
            }

            public void onProviderDisabled(String provider) {
                mCheckProvider(provider);
            }
        };

        //권한
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance,
                locationListener);
//        locationProvider = LocationManager.NETWORK_PROVIDER;
//        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance,
//                locationListener);




        //마커 클릭시 정보 보여주기
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplication()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                LatLng temp_lld = infoWindow.getMarker().getPosition();
                for (int i = 0; i < accidents.size(); i++) {

                    if (temp_lld.equals(accidents.get(i).getL_lld())) {
                        info_text = "위도 경도" + accidents.get(i).getLld() + "\n"
                                + "처리 유무" + accidents.get(i).getPro() + "\n"
                                + "비상 연락처" + accidents.get(i).getPhone();
                    }
                }
                return info_text;
            }
        });

        //지도를 클릭하면 닫음
        naverMap.setOnMapClickListener((coord, point) -> infoWindow.close());


        //마커 이벤트 클릭
        listener = overlay -> {
            Marker marker = (Marker) overlay;

            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };

        progressDialog.show();
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(String msg) {
                System.out.println("완료");
                check_loading = true;
                progressDialog.dismiss();
                mStartMap();
            }
        });


    }

    public void mAlertStatus(String provider) {
        Toast.makeText(this, "Locationn Services has been changed to" + provider,
                Toast.LENGTH_LONG).show();
    }

    public void mAlertProvider(String provider) {
        Toast.makeText(this, provider + ": location service is turn on!",
                Toast.LENGTH_LONG).show();
    }

    public void mCheckProvider(String provider) {
        Toast.makeText(this, provider + ": Please turn on location services...",
                Toast.LENGTH_LONG).show();
    }

    public void mStartMap() {
        System.out.println("업데이트시작");
        count = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("퍼미션 에러");
            return;
        }
        double dLatitude = locationManager.getLastKnownLocation(locationProvider).getLatitude();
        double dLongitude=locationManager.getLastKnownLocation(locationProvider).getLongitude();

        Current_location=new LatLng(dLatitude,dLongitude);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(Current_location)
                .animate(CameraAnimation.Fly, 1000);
        mMap.moveCamera(cameraUpdate);
        marker.setMap(null);
        marker.setPosition(Current_location);
        marker.setCaptionText("현재위치");
        marker.setWidth(100);
        marker.setHeight(80);


        System.out.println("사고마커 인덱스 크기 : "+markers.size());
        System.out.println("사고 인덱스 크기 : "+accidents.size());
        //로딩시 이미 마커가 있으면 새로 정보를 받기 위해 초기화
        if (markers.size()>0){
            for(int i=0; i<markers.size(); i++){
                markers.get(i).setMap(null);
            }
            markers.clear();
        }
        for(int i=0; i<accidents.size(); i++){
            Marker acc_marker=new Marker();
            acc_marker.setPosition(accidents.get(i).getL_lld());
            acc_marker.setCaptionText("Traffic accident");
            acc_marker.setWidth(100);
            acc_marker.setHeight(80);
            infoWindow.setPosition(accidents.get(i).getL_lld());


            markers.add(acc_marker);
            acc_marker.setOnClickListener(listener);
            acc_marker.setMap(mMap);
        }
        marker.setMap(mMap);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(locationManager!=null) {
            locationManager.removeUpdates(locationListener);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    private void readData(FirebaseCallback firebaseCallback){
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accidents.clear();
                check_loading=false;
                for (DataSnapshot postSnapshot: dataSnapshot.child(now_date).getChildren()) {
                    try {
                        ArrayList<String> value = (ArrayList<String>) postSnapshot.getValue();
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



}