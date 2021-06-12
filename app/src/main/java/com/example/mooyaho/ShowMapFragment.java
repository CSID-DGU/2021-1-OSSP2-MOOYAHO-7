package com.example.mooyaho;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class ShowMapFragment extends DialogFragment implements View.OnClickListener, OnMapReadyCallback {

    private double lat; // 현재 자신의 위치 위도
    private double lon; // 현재 자신의 위치 경도
    
    Marker marker1;
    private double startLatitude;
    private double startLongitude;

    Marker marker2;
    private double endLatitude;
    private double endLongitude;

    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    public static final String TAG_EVENT_DIALOG = "dialog_event";

    public ShowMapFragment () {};
    public static ShowMapFragment getInstance() {
        ShowMapFragment smf = new ShowMapFragment();
        return smf;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)  {
        // 오리지널에서 눌렀을 때 가져오는 것
        View v = inflater.inflate(R.layout.fragment_showmap, container);
        initialMap();
        // fragment 내 취소 확인 버튼
        Button mConfirmBtn = (Button) v.findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(this);
        setCancelable(false);
        return v;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        settingUI(naverMap);    // Ui setting

        myLocationSearchAndMarker(naverMap);    // 자신의 위치 찍기
        endLocationMarker(naverMap);   // 시작 위치 검색 후 찍기
        startLocationMarker(naverMap); // 도착 위치 검색 후 찍기
    }
    private void settingUI(@NonNull NaverMap naverMap) {

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 나침반
        uiSettings.setScaleBarEnabled(true);    // 거리 (축척)
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(true);  // 내가있는 곳
    }
    private void myLocationSearchAndMarker(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NotNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });
    }
    private void initialMap() {
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.small_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        marker1 = new Marker();
        marker2 = new Marker();
    }

    private void startLocationMarker(@NonNull NaverMap naverMap) {
        // start 위치 마커 찍기
        LatLng point = new LatLng(startLatitude, startLongitude);

        marker1.setPosition(point);
        marker1.setWidth(Marker.SIZE_AUTO);
        marker1.setHeight(Marker.SIZE_AUTO);
        marker1.setIconPerspectiveEnabled(true);
        marker1.setCaptionText("시작 위치");
        marker1.setCaptionAligns(Align.Top);

        marker1.setMap(naverMap);
    }

    private void endLocationMarker(@NonNull NaverMap naverMap) {
        // start 위치 마커 찍기
        LatLng point = new LatLng(startLatitude, startLongitude);

        marker1.setPosition(point);
        marker1.setWidth(Marker.SIZE_AUTO);
        marker1.setHeight(Marker.SIZE_AUTO);
        marker1.setIconPerspectiveEnabled(true);
        marker1.setCaptionText("시작 위치");
        marker1.setCaptionAligns(Align.Top);

        marker1.setMap(naverMap);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
