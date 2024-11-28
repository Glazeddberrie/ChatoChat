package com.chato.chatochat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chato.chatochat.models.Chat;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements ChatListAdapter.OnChatClickListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private String email;
    private TextView textWelcome;
    private ListenerRegistration chatListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        recyclerView = findViewById(R.id.recycler_chatlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textWelcome = findViewById(R.id.text_welcome);
        db = FirebaseFirestore.getInstance();
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList, this);
        email = getIntent().getStringExtra("email");

        recyclerView.setAdapter(chatListAdapter);

        cargarNombreUsuario();

        Button buttonNuevoChat = findViewById(R.id.btn_iniciar);
        buttonNuevoChat.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, NewChatActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        cargarChats();
    }

    private void cargarNombreUsuario() {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String name = document.getString("username");

                            if (name != null) {
                                textWelcome.setText("Bienvenido, " + name);
                            }
                        } else {
                            Toast.makeText(ChatListActivity.this, "No se encontró el usuario.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChatListActivity.this, "Error al cargar el nombre del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarChats() {
        chatListener = db.collection("chats")
                .whereArrayContains("participants", email)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(ChatListActivity.this, "Error al cargar los chats", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        chatList.clear();

                        for (DocumentSnapshot document : querySnapshot) {
                            String chatId = document.getId();
                            String chatName = document.getString("chatName");
                            String lastMessage = document.getString("lastMessage");
                            String lastMessageTime = document.getString("lastMessageTime");

                            List<String> participants = (List<String>) document.get("participants");
                            if (participants != null && participants.size() > 1) {
                                participants.remove(email);
                                chatName = participants.get(0);
                            }

                            Chat chat = new Chat(chatId, chatName, lastMessage, lastMessageTime);
                            chatList.add(chat);
                        }

                        chatListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatListActivity.this, "No tienes chats disponibles.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onChatClick(Chat chat) {
        String chatId = chat.getChatId();

        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) {
            chatListener.remove();
        }
    }
}