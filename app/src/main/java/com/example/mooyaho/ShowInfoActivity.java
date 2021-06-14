package com.example.mooyaho;

import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.annotations.NotNull;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class ShowInfoActivity extends AppCompatActivity  {

    private String information;
    private EditText info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showinfo);

        info = (EditText)findViewById(R.id.caution);
        info.setText("정보\n\n\n\n" +
                "간단한 전달을 목적으로 한 어플리케이션입니다. 사용자에게 친절히 대해주시고 전달의 보람을 느꼈으면 좋겠습니다.\n"+
                "간단한 문의는 ghldtjd901@dgu.ac.kr 로 연락바랍니다.\n"+
                "다음과 같은 상황이 일어날 경우 법적으로 엄중 대처하겠습니다.\n\n\n"+
                "제 335조(횡령, 배임)\n" +
                "1. 타인의 재물을 보관하는 자가 그 재물을 횡령하거나 그 반환을 거부한 때엔은 5년이하의 징역 또는 1천 500만원 이하의 벌금에 처한다.\n" +
                "2. 타인의 사무를 처리하는 자가 그 임무에 위배하는 행위로써 재산상의 이득을 취하거나 제삼자로 하여금 이를 취득하게 하여 본인에게 손해를 가한 때에도 전항의 형과 같다.\n");
    }
}
