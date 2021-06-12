package com.example.mooyaho;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mooyaho.data_class.PostResult;
import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowMapFragment extends DialogFragment implements View.OnClickListener, OnMapReadyCallback {

    List<PostResult> getInfo;

    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    // 접속할 IP 주소 = BASE_URL : 휴대폰으로 실행 시 나의 IP 주소
    // 이더넷 어댑터 이더넷 3 Ipv4 주소
    private  String BASE_URL = "http://10.90.0.110:3000";
    // 에뮬레이터로 실행 시(그냥 루프백 아이피라 보면 됨)
    //private  String BASE_URL = "http://10.0.2.2:3000";

    private double lat; // 현재 자신의 위치 위도
    private double lon; // 현재 자신의 위치 경도

    Marker marker1;
    private static double startLatitude;
    private static double startLongitude;

    Marker marker2;
    private static double endLatitude;
    private static double endLongitude;

    FragmentManager fm;

    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    public static final String TAG_EVENT_DIALOG = "dialog_event";

    private MapView mapView;

    PathOverlay path;

    public ShowMapFragment () {};
    public static ShowMapFragment getInstance(double startLat, double startLon, double endLat, double endLon) {
        startLatitude = startLat;
        startLongitude = startLon;
        endLatitude = endLat;
        endLongitude = endLon;
        ShowMapFragment smf = new ShowMapFragment();
        return smf;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)  {

        // 오리지널에서 눌렀을 때 가져오는 것
        View v = inflater.inflate(R.layout.fragment_showmap, container);
        mapView = v.findViewById(R.id.small_map);
        super.onCreate(savedInstanceState);
        // retrofit
        //retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
        //       .addConverterFactory(GsonConverterFactory.create()).build();

        //retrofitInterface = retrofit.create(RetrofitInterface.class);

        //CallBackGetInfo callBackGetInfo = new CallBackGetInfo() {
        //    @Override
        //    public void callBackForGetInfo(List<PostResult> lp) {
        //        for (int i = 0; i < getInfo.size(); i++) {
                    //startLatitude = String.valueOf(getInfo.get(i).getPostStartLatitude());
                    //startLatitude = String.valueOf(getInfo.get(i).getPostStartLongitude());
                    //endLatitude = String.valueOf(getInfo.get(i).getPostEndLatitude());
                    //endLongitude = String.valueOf(getInfo.get(i).getPostEndLongitude());
        //        }
        //    }
        //};

        //handleGetAll(callBackGetInfo);

        mapView.getMapAsync(this);

        initialMap();
        // fragment 내 취소 확인 버튼
        Button mConfirmBtn = (Button) v.findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(this);
        setCancelable(false);
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        settingUI(naverMap);    // Ui setting

        myLocationSearchAndMarker(naverMap);    // 자신의 위치 찍기
        endLocationMarker(naverMap);   // 시작 위치 검색 후 찍기
        startLocationMarker(naverMap); // 도착 위치 검색 후 찍기
        setPathLine(naverMap);
    }

    private void settingUI(@NonNull NaverMap naverMap) {
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 나침반
        uiSettings.setScaleBarEnabled(true);    // 거리 (축척)
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(true);  // 내가있는 곳
    }

    private void setPathLine(NaverMap naverMap){
        path.setCoords(Arrays.asList(
                new LatLng(startLatitude, startLongitude),
                new LatLng(endLatitude, endLongitude)
        ));
        path.setMap(naverMap);
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
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        marker1 = new Marker();
        marker2 = new Marker();
        getInfo = new ArrayList<PostResult>();
        path = new PathOverlay();
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
        LatLng point = new LatLng(endLatitude, endLongitude);

        marker2.setPosition(point);
        marker2.setWidth(Marker.SIZE_AUTO);
        marker2.setHeight(Marker.SIZE_AUTO);
        marker2.setIconPerspectiveEnabled(true);
        marker2.setCaptionText("도착 위치");
        marker2.setCaptionAligns(Align.Top);

        marker2.setMap(naverMap);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
    //private void handleGetAll(CallBackGetInfo callBackGetInfo){
    //    Call<List<PostResult>> call = retrofitInterface.getAll(); // getAll로 서버와 통신
    //    call.enqueue(new Callback<List<PostResult>>() {
    //        @Override
    //        public void onResponse(Call<List<PostResult>> call, Response<List<PostResult>> response) {
    //           getInfo = response.body(); // response.body에는 모든 요청 객체가 배열로 담겨져 있음
    //            callBackGetInfo.callBackForGetInfo(getInfo);
    //       }
    //
    //  @Override
    //        public void onFailure(Call<List<PostResult>> call, Throwable t) { }
    //    });
    //}
}
