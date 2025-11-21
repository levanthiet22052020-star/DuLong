package com.example.dulong

import android.os.Bundle
import android.view.View // [Mới] Import View để dùng Visible/Gone
import android.widget.ImageView // [Mới] Import ImageView
import android.widget.LinearLayout // [Mới] Import LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val categoryOverlay = findViewById<LinearLayout>(R.id.categoryOverlay)

        btnMenu.setOnClickListener {
            if (categoryOverlay.visibility == View.VISIBLE) {
                categoryOverlay.visibility = View.GONE
            } else {
                categoryOverlay.visibility = View.VISIBLE
                categoryOverlay.bringToFront()
            }
        }
        categoryOverlay.setOnClickListener {
            categoryOverlay.visibility = View.GONE
        }
    }
}