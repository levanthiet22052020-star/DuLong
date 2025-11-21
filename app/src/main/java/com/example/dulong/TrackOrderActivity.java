package com.example.dulong;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TrackOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressedCustom());
        // Nếu sau này bạn muốn set dữ liệu động:
        // TextView tvEstimateDate = findViewById(R.id.tvEstimateDate);
        // TextView tvCarrierName = findViewById(R.id.tvCarrierName);
        // TextView tvTrackingCode = findViewById(R.id.tvTrackingCode);
        // ... rồi setText(...) theo dữ liệu đơn hàng.
    }

    private void onBackPressedCustom() {
        finish();
    }
}
