package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etpasswordConfirm;
    private TextView tvError;

    private Button btnSignup, btnGoToLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etEmail = findViewById(R.id.et_signup_email);
        etPassword = findViewById(R.id.ev_signup_password);
        etpasswordConfirm = findViewById(R.id.ev_signup_password_confirm);
        btnSignup = findViewById(R.id.btn_signup);
        btnGoToLogin = findViewById(R.id.btn_signup_login);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
        tvError = findViewById(R.id.tv_login_error);
        firebaseAuth = FirebaseAuth.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String passwordConfirm = etpasswordConfirm.getText().toString();
                if (validateInput(email, password, passwordConfirm)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                            } else {
                                tvError.setText(task.getException().getMessage());
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
            }
        });

    }

    private boolean validateInput(String email, String password, String confirm) {

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("email cant' be empty");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("password cant' be empty");
            return false;
        }
        if (!password.equals(confirm)) {
            etpasswordConfirm.setError("password didn't match");

            return false;
        }
        return true;
    }
}
