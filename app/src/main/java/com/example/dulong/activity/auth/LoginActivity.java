package com.example.dulong.activity.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dulong.R;
import com.example.dulong.activity.HomeActivity;
import com.example.dulong.activity.admin.AdminDashboardActivity;
import com.example.dulong.api.RetrofitClient;
import com.example.dulong.model.LoginRequest;
import com.example.dulong.model.LoginResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnGoogle;
    private TextView tvSignupLink;
    private ImageView ivPassToggle;
    private TextView tvForgot;

    private boolean isPasswordVisible = false;
    
    // Google Sign-In
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupGoogleSignIn();
        addEvents();

        String registeredPhone = getIntent().getStringExtra("register_phone");
        if (registeredPhone != null) {
            etUsername.setText(registeredPhone);
            etPassword.requestFocus();
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
        tvSignupLink = findViewById(R.id.tv_signup_link);
        ivPassToggle = findViewById(R.id.iv_pass_toggle);
        tvForgot = findViewById(R.id.tv_forgot);
    }
    
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
                
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Initialize ActivityResultLauncher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                }
        );
    }

    private void addEvents() {
        btnLogin.setOnClickListener(v -> handleLogin());
        
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        tvSignupLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        ivPassToggle.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivPassToggle.setImageResource(R.drawable.ic_eye_24);
            isPasswordVisible = false;
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            // ivPassToggle.setImageResource(R.drawable.ic_eye_off_24);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void handleLogin() {
        String account = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();

        LoginRequest request = new LoginRequest(account, password);

        // Gọi API
        RetrofitClient.getInstance().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.isStatus()) {
                        // --- ĐĂNG NHẬP THÀNH CÔNG ---

                        LoginResponse.User user = loginResponse.getUser();

                        // 1. LƯU ID VÀO BỘ NHỚ
                        SharedPreferences sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_ID", user != null ? user.get_id() : null);
                        editor.putString("USER_ROLE", user != null ? user.getRole() : null);
                        editor.putString("USER_NAME", user != null ? user.getUsername() : null);
                        editor.putString("USER_PHONE", user != null ? user.getPhone() : null);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Xin chào " + (user != null ? user.getUsername() : ""), Toast.LENGTH_SHORT).show();

                        // 2. Phân quyền (Role)
                        String role = user != null ? user.getRole() : "user";

                        if ("admin".equals(role)) {
                            Log.d("LOGIN_APP", "User is Admin");
                            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("LOGIN_APP", "User is Customer");
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }

                        finish();

                    } else {
                        String msg = loginResponse != null ? loginResponse.getMessage() : "Đăng nhập thất bại";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginError", t.getMessage() != null ? t.getMessage() : "Unknown error");
            }
        });
    }
    
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }
    
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            
            // Lấy ID Token từ Google account
            String idToken = account.getIdToken();
            String email = account.getEmail();
            String name = account.getDisplayName();
            
            Log.d("GoogleSignIn", "Email: " + email);
            Log.d("GoogleSignIn", "Name: " + name);
            Log.d("GoogleSignIn", "ID Token: " + (idToken != null ? "Có" : "Không có"));
            
            if (idToken == null) {
                Toast.makeText(this, "Không thể lấy ID Token từ Google", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Gửi ID Token lên server để verify
            sendTokenToServer(idToken);
            
        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            String errorMsg = "Đăng nhập Google thất bại: " + e.getStatusCode();
            if (e.getStatusCode() == 10) {
                errorMsg = "Lỗi cấu hình Google Sign-In. Vui lòng kiểm tra SHA-1 fingerprint.";
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }
    
    private void sendTokenToServer(String idToken) {
        Toast.makeText(this, "Đang xác thực với Google...", Toast.LENGTH_SHORT).show();
        
        // Tạo request với ID Token
        Map<String, String> request = new HashMap<>();
        request.put("idToken", idToken);
        
        // Gọi API Google login với ID Token
        RetrofitClient.getInstance().googleLogin(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.isStatus()) {
                        LoginResponse.User user = loginResponse.getUser();
                        
                        // Lưu thông tin user
                        SharedPreferences sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_ID", user != null ? user.get_id() : null);
                        editor.putString("USER_ROLE", user != null ? user.getRole() : null);
                        editor.putString("USER_NAME", user != null ? user.getUsername() : null);
                        editor.putString("USER_PHONE", user != null ? user.getPhone() : null);
                        editor.putString("USER_EMAIL", user != null ? user.getEmail() : null);
                        editor.apply();
                        
                        Toast.makeText(LoginActivity.this, "Chào mừng " + (user != null ? user.getUsername() : ""), Toast.LENGTH_SHORT).show();
                        
                        // Phân quyền
                        String role = user != null ? user.getRole() : "user";
                        if ("admin".equals(role)) {
                            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        
                        finish();
                        
                    } else {
                        String msg = loginResponse != null ? loginResponse.getMessage() : "Đăng nhập Google thất bại";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("GoogleLogin", "Server error: " + errorBody);
                        Toast.makeText(LoginActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GoogleLoginError", t.getMessage() != null ? t.getMessage() : "Unknown error");
            }
        });
    }
}