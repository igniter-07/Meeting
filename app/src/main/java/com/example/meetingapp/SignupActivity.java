package com.example.meetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.meetingapp.databinding.ActivitySignupBinding;
import com.example.meetingapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    ActivitySignupBinding binding;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(SignupActivity.this);
        dialog.setMessage("Login...");
        dialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SignupActivity.this, FingerPrintActivity.class);
            startActivity(intent);
            finish();
        }

        binding.btnAreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String name = binding.edtFullName.getText().toString();
                String password = binding.etvPassword.getText().toString();
                String email = binding.etvEmail.getText().toString();

                if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                    User user = new User(name, email, password);

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        dialog.dismiss();
                                        firebaseFirestore.collection("Users")
                                                .document()
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                        Toast.makeText(SignupActivity.this, "Account created Successfully...", Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignupActivity.this, "Fields can't be empty!!!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}