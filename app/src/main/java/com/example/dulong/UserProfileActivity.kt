package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        val btnDetail = findViewById<ImageView>(R.id.btnDetail)
        val btnEdit = findViewById<ImageView>(R.id.btnEdit)
        val btnCart = findViewById<ImageView>(R.id.iv_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnDetail.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, TrackOrderActivity::class.java)
            startActivity(intent)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, AddressListActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
        val intent = Intent(this@UserProfileActivity, CardActivity::class.java)
        startActivity(intent)
        }

    }
}