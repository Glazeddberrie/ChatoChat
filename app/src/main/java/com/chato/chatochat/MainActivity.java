package com.chato.chatochat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private String email;
    private Button btn_ingresar;
    private Button btn_registrar;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_ingresar = findViewById(R.id.btn_ingresar);
        btn_registrar = findViewById(R.id.btn_registrar);
        editTextEmail = findViewById(R.id.txt_username);
        editTextPassword = findViewById(R.id.txt_pass);

        btn_ingresar.setOnClickListener(v -> {

            email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                iniciarSesion(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }

        });

        btn_registrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        verificarNombreDeUsuario(email);
                    } else {
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarNombreDeUsuario(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        boolean usernameExists = false;

                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.contains("username") && document.get("username") != null) {
                                usernameExists = true;
                                break;
                            }
                        }

                        if (usernameExists) {
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor, selecciona un nombre de usuario.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, SelectUsernameActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontró un usuario con este correo electrónico.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}