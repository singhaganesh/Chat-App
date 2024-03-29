package com.example.chatapp.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.Models.ChatMessageModel;
import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatapp.databinding.ItemContainerSendMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Bitmap receiverProfileImage;
    private final List<ChatMessageModel> chatMessageModels;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(Bitmap receiverProfileImage, List<ChatMessageModel> chatMessageModels, String senderId) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessageModels = chatMessageModels;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_send_message,parent,false));
        }
        else {
            return new ReceivedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessageModels.get(position));
        }else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessageModels.get(position),receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageModels.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSendMessageBinding binding;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerSendMessageBinding.bind(itemView);
        }


        void setData(ChatMessageModel chatMessageModel){
            binding.textMessage.setText(chatMessageModel.message);
            binding.textDateTime.setText(chatMessageModel.dateTime);
        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContainerReceivedMessageBinding.bind(itemView);
        }


        void setData(ChatMessageModel chatMessageModel,Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessageModel.message);
            binding.textDateTime.setText(chatMessageModel.dateTime);
            if (receiverProfileImage != null){
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }

        }
    }

}
