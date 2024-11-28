package com.chato.chatochat;

import static com.chato.chatochat.R.layout.activity_register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private Button btnRegistrar;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassConfirm;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activity_register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegistrar = findViewById(R.id.btn_registrar);
        editTextEmail = findViewById(R.id.txt_username);
        editTextPassword = findViewById(R.id.txt_pass);
        editTextPassConfirm = findViewById(R.id.txt_confpass);

        btnRegistrar.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String passConfirm = editTextPassConfirm.getText().toString().trim();

            if (validateInputs(email, password, passConfirm)) {
                registerUser(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password, String passConfirm) {
        if (email.isEmpty()) {
            editTextEmail.setError("El correo electrónico es obligatorio");
            editTextEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Por favor, introduce un correo electrónico válido");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("La contraseña es obligatoria");
            editTextPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("La contraseña debe tener al menos 6 caracteres");
            editTextPassword.requestFocus();
            return false;
        }

        if (passConfirm.isEmpty()) {
            editTextPassConfirm.setError("Por favor, confirma tu contraseña");
            editTextPassConfirm.requestFocus();
            return false;
        }
        if (!password.equals(passConfirm)) {
            editTextPassConfirm.setError("Las contraseñas no coinciden");
            editTextPassConfirm.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("AUTH", "createUserWithEmail:success");
                            addUserToFirestore(email);
                        } else {
                            Log.w("AUTH", "createUserWithEmail:failure", task.getException());
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                            Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addUserToFirestore(String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", null);

        db.collection("users")
                .add(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIRESTORE", "Usuario añadido a Firestore con éxito");
                        Toast.makeText(RegisterActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Log.w("FIRESTORE", "Error al añadir usuario a Firestore", task.getException());
                        Toast.makeText(RegisterActivity.this, "Error al guardar en Firestore: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}