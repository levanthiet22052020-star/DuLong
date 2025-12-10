package com.example.dulong.activity.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dulong.R
import com.example.dulong.activity.auth.ChangePasswordActivity
import com.example.dulong.activity.auth.LoginActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnChangePassword = findViewById<LinearLayout>(R.id.btnChangePassword)
        val btnManageAddress = findViewById<LinearLayout>(R.id.btnManageAddress)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnBack.setOnClickListener {
            finish()
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        btnManageAddress.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}