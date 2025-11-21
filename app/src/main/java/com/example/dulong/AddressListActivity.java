package com.example.dulong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class AddressListActivity extends AppCompatActivity {

    RecyclerView recycler;
    AddressAdapter adapter;
    List<AddressModel> list;
    LinearLayout btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        recycler = findViewById(R.id.recyclerAddress);
        btnAddNew = findViewById(R.id.btnAddNew);

        // Fake data
        list = new ArrayList<>();
        list.add(new AddressModel("Nguyễn Văn A", "(+84) 123456789",
                "Số nhà 32/11, Đường ABC, P4, Q12, TP.HCM"));
        list.add(new AddressModel("Nguyễn Văn B", "(+84) 987654321",
                "Số nhà 32/11, Đường ABC, P4, Q12, TP.HCM"));

        adapter = new AddressAdapter(list, model -> {
            Intent i = new Intent(AddressListActivity.this, EditAddressActivity.class);
            i.putExtra("mode", "edit");
            i.putExtra("name", model.getName());
            i.putExtra("phone", model.getPhone());
            i.putExtra("address", model.getAddress());
            startActivity(i);
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressedCustom());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnAddNew.setOnClickListener(v -> {
            Intent i = new Intent(AddressListActivity.this, EditAddressActivity.class);
            i.putExtra("mode", "add");
            startActivity(i);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    private void onBackPressedCustom() {
        finish();
    }
}

