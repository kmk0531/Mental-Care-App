package com.example.tp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatList;

    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    // 뷰타입 구분 (사용자 or GPT)
    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_gpt, parent, false);
            return new GptViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textView.setText(message.getMessage());
        } else {
            ((GptViewHolder) holder).textView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        UserViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }

    static class GptViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        GptViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }
}
