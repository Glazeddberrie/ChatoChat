package com.chato.chatochat;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chato.chatochat.models.Message;
import com.chato.chatochat.models.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String chatId;
    private EditText inputMessage;
    private ImageButton sendButton;
    private String senderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inputMessage = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.sendButton);

        db = FirebaseFirestore.getInstance();
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        chatId = getIntent().getStringExtra("chatId");
        senderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        escucharMensajes();

        sendButton.setOnClickListener(v -> {
            String messageContent = inputMessage.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                enviarMensaje(messageContent);
            }
        });
    }

    private void escucharMensajes() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ChatActivity", "Error al escuchar mensajes", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        messageList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            if (message != null) {
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void enviarMensaje(String content) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message(senderEmail, content, timestamp);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    actualizarUltimoMensaje(content, timestamp);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                });
        inputMessage.setText("");
    }


    private void actualizarUltimoMensaje(String content, long timestamp) {
        String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));

        db.collection("chats")
                .document(chatId)
                .update("lastMessage", content, "lastMessageTime", formattedTime)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChatActivity", "Último mensaje actualizado");
                })
                .addOnFailureListener(e -> {
                    Log.w("ChatActivity", "Error al actualizar el último mensaje", e);
                });
    }
}