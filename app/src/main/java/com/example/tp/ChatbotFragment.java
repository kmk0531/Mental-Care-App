package com.example.tp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private RecyclerView chatRecycler;
    private EditText editInput;
    private ImageButton sendButton;
    private List<ChatMessage> chatList;
    private ChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        chatRecycler = view.findViewById(R.id.chat_recycler);
        editInput = view.findViewById(R.id.edit_input);
        sendButton = view.findViewById(R.id.btn_send);

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList);
        chatRecycler.setAdapter(adapter);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        sendButton.setOnClickListener(v -> {
            String userInput = editInput.getText().toString().trim();
            if (!userInput.isEmpty()) {
                chatList.add(new ChatMessage(userInput, ChatMessage.TYPE_USER));
                adapter.notifyItemInserted(chatList.size() - 1);
                chatRecycler.scrollToPosition(chatList.size() - 1);
                editInput.setText("");

                // GPT 응답 예시
                chatList.add(new ChatMessage("GPT 응답입니다", ChatMessage.TYPE_GPT));
                adapter.notifyItemInserted(chatList.size() - 1);
                chatRecycler.scrollToPosition(chatList.size() - 1);
            }
        });

        return view;
    }
}
