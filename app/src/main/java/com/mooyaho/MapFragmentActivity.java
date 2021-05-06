package com.mooyaho;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource; // 현재 위치 반환
    private NaverMap naverMap;
    private MapView mapView;
    private double lat, lon; // 위도 경도

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapBasicSettings();
        // 런타임 권한 처리를 위해 생성자에 액티비티 객체 전달, 권한요청 코드 지정
        locationSource = new FusedLocationSource(this ,LOCATION_PERMISSION_REQUEST_CODE);

    }


    public void mapBasicSettings() {
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap){
        this.naverMap = naverMap;

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

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                // 위치 변경시 이것을 수행
                lat = location.getLatitude();
                lon = location.getLongitude();

                //출력
                Toast.makeText(getApplicationContext(), lat + ", " + lon, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults){
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)){
            if(!locationSource.isActivated()){  // 권한 거부시
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
