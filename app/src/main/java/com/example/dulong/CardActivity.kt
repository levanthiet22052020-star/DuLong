package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class CardActivity : AppCompatActivity() {
    private lateinit var btnCheckout: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        btnCheckout = findViewById(R.id.btnCheckout)
        // Liên kết với layout activity_card.xml
        setContentView(R.layout.activity_card)

        // Xử lý giao diện tràn viền
        // Lưu ý: Trong file activity_card.xml, thẻ gốc (root) phải có id là "@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCheckout.setOnClickListener {
            Toast.makeText(this@CardActivity, "Mua hàng thành công}", Toast.LENGTH_SHORT).show()
        }
    }
}