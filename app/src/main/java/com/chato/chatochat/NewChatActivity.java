package com.chato.chatochat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chato.chatochat.models.Chat;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class NewChatActivity extends AppCompatActivity {

    private EditText editTextSearchUser;
    private Button buttonStartChat;
    private TextView textViewUserFound;
    private FirebaseFirestore db;

    private String email;
    private String otherUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        editTextSearchUser = findViewById(R.id.editTextSearchUser);
        buttonStartChat = findViewById(R.id.buttonStartChat);
        textViewUserFound = findViewById(R.id.textViewUserFound);
        db = FirebaseFirestore.getInstance();

        email = getIntent().getStringExtra("email");

        editTextSearchUser.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String searchEmail = editTextSearchUser.getText().toString().trim();
                if (!searchEmail.isEmpty()) {
                    buscarUsuario(searchEmail);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        buttonStartChat.setOnClickListener(v -> iniciarChat());
    }

    private void buscarUsuario(String emailBuscado) {
        db.collection("users")
                .whereEqualTo("email", emailBuscado)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            otherUserEmail = emailBuscado;
                            textViewUserFound.setText("Usuario encontrado: " + emailBuscado);
                            buttonStartChat.setEnabled(true);
                        }
                    } else {
                        textViewUserFound.setText("No se encontró al usuario.");
                        buttonStartChat.setEnabled(false);
                    }
                });
    }

    private void iniciarChat() {
        if (otherUserEmail != null && !otherUserEmail.equals(email)) {
            String chatId = db.collection("chats").document().getId();

            List<String> participantes = Arrays.asList(email, otherUserEmail);

            db.collection("chats").document(chatId)
                    .set(new Chat(chatId, "Chat con " + otherUserEmail, "", null))
                    .addOnSuccessListener(aVoid -> {
                        db.collection("chats").document(chatId)
                                .update("participants", participantes)
                                .addOnSuccessListener(aVoid1 -> {
                                    Intent intent = new Intent(NewChatActivity.this, ChatActivity.class);
                                    intent.putExtra("chatId", chatId);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(NewChatActivity.this, "Error al iniciar el chat", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
