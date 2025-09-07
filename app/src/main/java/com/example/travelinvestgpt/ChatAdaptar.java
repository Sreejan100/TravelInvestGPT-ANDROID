package com.example.travelinvestgpt;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.cloudinary.Configuration;

import java.util.List;

public class ChatAdaptar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    public static final int VIEW_TYPE_USER=1;
    public static final int VIEW_TYPE_BOT = 2;

    public ChatAdaptar(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? VIEW_TYPE_USER :VIEW_TYPE_BOT;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message,parent,false);
            return new UserMessageViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message,parent,false);
            return new BotMessageViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).textMessage.setText(message.getText());
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).textMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.usermessage);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.botmessage);
        }
    }

}
