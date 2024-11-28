package com.chato.chatochat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectUsernameActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        editTextUsername = findViewById(R.id.txt_username);
        btnGuardar = findViewById(R.id.btn_username);
        db = FirebaseFirestore.getInstance();

        String email = getIntent().getStringExtra("email");

        btnGuardar.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            if (!username.isEmpty()) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    db.collection("users")
                                            .document(document.getId())
                                            .update("username", username)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(SelectUsernameActivity.this, "Nombre de usuario guardado.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SelectUsernameActivity.this, ChatListActivity.class);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SelectUsernameActivity.this, "Error al guardar el nombre de usuario.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            } else {
                Toast.makeText(SelectUsernameActivity.this, "Por favor, ingresa un nombre de usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}