package com.mooyaho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 지도를 출력하기 위한 설정 */
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        naverMapBasicSettings();
        /* 지도를 출력하기 위한 설정 */
    }

    public void naverMapBasicSettings() {
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap){
        // 현재 위치 버튼 안보이게 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);

        // 지도 유형을 위성사진으로 설정
        naverMap.setMapType(NaverMap.MapType.Basic);
        // 위성사진은 Satellite
        // 차량용 네비 Navi
        // 일반지도 Basic
        // 위성사진+도로, 심벌 Hybrid
        // 지형도(산악지형을 유사하게 표현) Terrain
        // 지도를 나타내지 않지만 오버레이는 나타냄 None
    }
}