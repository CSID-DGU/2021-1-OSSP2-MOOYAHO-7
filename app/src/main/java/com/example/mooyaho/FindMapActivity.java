package com.example.mooyaho;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindMapActivity extends AppCompatActivity implements OnMapReadyCallback  {

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
    private Button myLocationbutton1;
    private Button myLocationbutton2;

    private String errorMessage = "can't find that";

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);
        initialMap();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        settingUI(naverMap);    // Ui setting

        myLocationSearchAndMarker(naverMap);    // 자신의 위치 찍기
        endLocationSearchAndMarker(naverMap);   // 시작 위치 검색 후 찍기
        startLocationSearchAndMarker(naverMap); // 도착 위치 검색 후 찍기
        postData();
    }
    private void postData(){
        Intent myIntent = new Intent(this, DeliverRequestActivity.class);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEdit1 = startText.getText().toString();
                String getEdit2 = endText.getText().toString();

                if(getEdit1.getBytes().length <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "시작 주소을 채워주세요",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(getEdit2.getBytes().length <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "도착 주소을 채워주세요",
                            Toast.LENGTH_LONG).show();
                   return;
                }

                String startLocation = ((EditText) findViewById(R.id.start_loc)).getText().toString();
                String endLocation = ((EditText) findViewById(R.id.end_loc)).getText().toString();

                myIntent.putExtra("start", startLocation);
                myIntent.putExtra("end", endLocation);
                myIntent.putExtra("startLat", startLatitude);
                myIntent.putExtra("startLon", startLongitude);
                myIntent.putExtra("endLat", endLatitude);
                myIntent.putExtra("endLon", endLongitude);
                startActivity(myIntent);
            }
        });
    }
    private void myLocationSearchAndMarker(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NotNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                setToMyLocation();        // 자신 위치 검색 후 찍기
            }
        });
    }

    private void setToMyLocation() {

        myLocationbutton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                List<Address> list = null;
                try {
                    System.out.println("lat: " + lat + "lon: " + lon);
                    list = geocoder.getFromLocation(lat, lon, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","my location error");
                }
                if(list != null) {
                    if(list.size() == 0){
                        System.out.println("size = " + list.size());
                        System.out.println(list.get(0).toString());
                        Toast.makeText(getApplicationContext(),
                                "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        startText.setText(list.get(0).getAddressLine(0));
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                        "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                        Toast.LENGTH_LONG).show();

                }
            }
        });

        myLocationbutton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                List<Address> list = null;
                try {
                    System.out.println("lat: " + lat + "lon: " + lon);
                    list = geocoder.getFromLocation(lat, lon, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","my location error");
                }
                if(list != null) {
                    if(list.size() == 0){
                        System.out.println("size = " + list.size());
                        System.out.println(list.get(0).toString());
                        Toast.makeText(getApplicationContext(),
                                "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        endText.setText(list.get(0).getAddressLine(0));
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "해당 되는 주소정보가 없습니다. 좀 더 자세히 입력해주세요",
                            Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void startLocationSearchAndMarker(@NonNull NaverMap naverMap) {
        // start 위치 마커 찍기
        sbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                String str = startText.getText().toString();    // 여기서 startText 값 넣기
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
    }

    private void endLocationSearchAndMarker(@NonNull NaverMap naverMap) {
        // end 위치 마커 찍기
        ebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                List<Address> list = null;

                String str = endText.getText().toString();    // 여기서 endText 값 넣기
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
    private void settingUI(@NonNull NaverMap naverMap) {

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 나침반
        uiSettings.setScaleBarEnabled(true);    // 거리 (축척)
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(true);  // 내가있는 곳
    }
    private void initialMap() {
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.small_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        tv = (TextView)findViewById(R.id.resultArea);
        startText = (EditText)findViewById(R.id.start_loc);
        endText = (EditText)findViewById(R.id.end_loc);

        marker1 = new Marker();
        marker2 = new Marker();
        geocoder = new Geocoder(this);

        sbutton = (Button)findViewById(R.id.startButton);
        ebutton = (Button)findViewById(R.id.endButton);
        myLocationbutton1 = (Button)findViewById((R.id.my_location1));
        myLocationbutton2 = (Button)findViewById((R.id.my_location2));

        submitButton = (Button)findViewById(R.id.getLoc);
    }
}
