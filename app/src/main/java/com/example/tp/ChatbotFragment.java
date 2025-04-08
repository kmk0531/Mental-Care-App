package com.example.tp;

import android.os.Bundle;
import android.util.Log;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private RecyclerView chatRecycler;
    private EditText editInput;
    private ImageButton sendButton;
    private List<ChatMessage> chatList;
    private ChatAdapter adapter;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        chatRecycler = view.findViewById(R.id.chat_recycler);
        editInput = view.findViewById(R.id.edit_input);
        sendButton = view.findViewById(R.id.btn_send);
        requestQueue = Volley.newRequestQueue(requireContext());

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
                sendMessageToGPT(userInput);
            }
        });

        return view;

    }

    private void sendMessageToGPT(String message) {
        String url = "http://10.0.2.2:3000/gpt";  // 서버 주소
        JSONObject body = new JSONObject();

        try {
            body.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    String reply = response.optString("reply");
                    Log.d("GPT", "응답: " + reply);
                    // 👉 RecyclerView에 대화 추가하거나 화면에 표시
                    chatList.add(new ChatMessage(reply, ChatMessage.TYPE_GPT));
                    adapter.notifyItemInserted(chatList.size() - 1);
                    chatRecycler.scrollToPosition(chatList.size() - 1);
                },
                error -> {
                    error.printStackTrace();
                    Log.e("GPT", "요청 실패: " + error.toString());
                }
        );

        requestQueue.add(request);
    }

}


