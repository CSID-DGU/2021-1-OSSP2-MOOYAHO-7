package com.example.mooyaho;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class FindMapActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private double lat;
    private double lon;

    private EditText startText;
    private EditText endText;
    private String errorMessage = "can't find that";

    private Geocoder geo;
    private List<Address> address;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmap);
        initialMap();
    }

    public String bringStart() {
        EditText et;
        et = (EditText)findViewById(R.id.start_loc);
        return et.getText().toString();
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
            public void onLocationChange(@NotNull Location location){
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });
        findCoordinates();
    }

    public void findCoordinates(){
        try {
            address = geo.getFromLocationName(bringStart(), 10);
        } catch (IOException e){
            e.printStackTrace();
            Log.d("test", "IO error");
        }
        if(address != null){
            if(address.size() == 0){
                startText.setText(errorMessage);
            }
            else {
                Log.d("find address", address.get(0).toString());
                startText.setText(address.get(0).getAddressLine(0));
            }
        }
        marker.setPosition(new LatLng(lat, lon));
        marker.setMap(naverMap);
    }

    public void findTextAddress(){
        try {
            address = geo.getFromLocation(lat, lon, 10);
        } catch (IOException e){
            e.printStackTrace();
            Log.d("test", "IO error");
        }
        if(address != null){
            if(address.size() == 0){
            }
            else {
                Log.d("find address", address.get(0).toString());
                startText.setText(address.get(0).getAddressLine(0));
            }
        }
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

        geo = new Geocoder(this);
        address = null;

        startText = (EditText) findViewById(R.id.start_loc);
        endText = (EditText) findViewById(R.id.end_loc);
        marker = new Marker();
    }

}
