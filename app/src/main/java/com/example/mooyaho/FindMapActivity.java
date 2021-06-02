package com.example.mooyaho;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;

public class FindMapActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private NaverMap naverMap;  // 하나의 map 객체
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    // 자신 위치
    private double lat; 
    private double lon;
    // 시작 위치
    Marker marker1;
    private double startLatitude;
    private double startLongitude;
    // 도착 위치
    Marker marker2;
    private double endLatitude;
    private double endLongitude;

    private TextView tv;
    private EditText startText;
    private EditText endText;

    boolean startIsEmpty = false;
    boolean endIsEmpty = false;

    private Button sbutton;
    private Button ebutton;
    private Button submitButton;

    private String errorMessage = "can't find that";

    private Geocoder geocoder = new Geocoder(this, Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);
        initialMap();
    }

    public String bringStart() {
        EditText et;
        et = (EditText)findViewById(R.id.start_loc);
        return et.toString();
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 나침반
        uiSettings.setScaleBarEnabled(true);    // 거리 (축척)
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(true);  // 내가있는 곳
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NotNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });
        // start 위치 마커 찍기
        sbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                String str = startText.getText().toString();
                try {
                    list = geocoder.getFromLocationName(str, 10);
                } catch (IOException e){
                    e.printStackTrace();
                    Log.e("test", "입출력 오류");
                }

                if(list != null){
                    if(list.size() == 0){
                        Toast.makeText(getApplicationContext(),
                                "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                                Toast.LENGTH_LONG).show();
                        startIsEmpty = true;
                    } else {
                        startIsEmpty = false;
                        // 주소를 받아왔으면, 위치값 저장
                        double d1 = list.get(0).getLatitude();
                        double d2 = list.get(0).getLongitude();

                        LatLng point = new LatLng(d1, d2);
                        startLatitude = d1;
                        startLongitude = d2;
                        System.out.println(d1 +", "+ d2);
                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(
                                point, 15).animate(CameraAnimation.Fly, 1000);
                        naverMap.moveCamera(cameraUpdate);
                        marker1.setPosition(point);
                        marker1.setWidth(Marker.SIZE_AUTO);
                        marker1.setHeight(Marker.SIZE_AUTO);
                        marker1.setIconPerspectiveEnabled(true);
                        marker1.setCaptionText("시작 위치");
                        marker1.setCaptionAligns(Align.Top);

                        marker1.setMap(naverMap);
                    }
                }
            }
        });
        // end 위치 마커 찍기
        ebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                List<Address> list = null;

                String str = endText.getText().toString();
                try {
                    list = geocoder.getFromLocationName(str, 10);
                } catch (IOException e){
                    e.printStackTrace();
                    Log.e("test", "입출력 오류");
                }

                if(list != null){
                    if(list.size() == 0){
                        Toast.makeText(getApplicationContext(),
                            "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                            Toast.LENGTH_LONG).show();
                        endIsEmpty = true;
                    } else {
                        endIsEmpty = false;
                        // 주소를 받아왔으면, 위치값 저장
                        double d1 = list.get(0).getLatitude();
                        double d2 = list.get(0).getLongitude();
                        LatLng point = new LatLng(d1, d2);
                        endLatitude = d1;
                        endLongitude = d2;
                        System.out.println(d1 +", "+ d2);

                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(
                                point, 15).animate(CameraAnimation.Fly, 1000);
                        naverMap.moveCamera(cameraUpdate);
                        marker2.setPosition(point);
                        marker2.setWidth(Marker.SIZE_AUTO);
                        marker2.setHeight(Marker.SIZE_AUTO);
                        marker2.setIconPerspectiveEnabled(true);
                        marker2.setCaptionText("도착 위치");
                        marker2.setCaptionAligns(Align.Top);

                        marker2.setMap(naverMap);
                    }
                }
            }
        });

    }

    private void initialMap(){
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        tv = (TextView)findViewById(R.id.resultArea);
        startText = (EditText)findViewById(R.id.start_loc);
        endText = (EditText)findViewById(R.id.end_loc);

        marker1 = new Marker();
        marker2 = new Marker();

        sbutton = (Button)findViewById(R.id.startButton);
        ebutton = (Button)findViewById(R.id.endButton);
        submitButton = (Button)findViewById(R.id.submit);
    }
}
