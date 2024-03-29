package com.example.chatapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.OnlineUserModel;
import com.example.chatapp.databinding.ItemUserOnlineStatusBinding;
import java.util.List;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.OnlineUserViewHolder>{

    private final List<OnlineUserModel> onlineUserModels;

    public OnlineUserAdapter(List<OnlineUserModel> onlineUserModels){
        this.onlineUserModels = onlineUserModels;
    }

    @NonNull
    @Override
    public OnlineUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserOnlineStatusBinding itemUserOnlineStatusBinding = ItemUserOnlineStatusBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new OnlineUserViewHolder(itemUserOnlineStatusBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineUserViewHolder holder, int position) {
            holder.setUserDate(onlineUserModels.get(position));
    }

    @Override
    public int getItemCount() {
        return onlineUserModels.size();
    }

    class OnlineUserViewHolder extends RecyclerView.ViewHolder{

        ItemUserOnlineStatusBinding binding;

        OnlineUserViewHolder(ItemUserOnlineStatusBinding itemUserOnlineStatusBinding){
            super(itemUserOnlineStatusBinding.getRoot());
            binding = itemUserOnlineStatusBinding;
        }
        void setUserDate(OnlineUserModel onlineUserModel){
            binding.userName.setText(onlineUserModel.getUserName());
            binding.imageProfile.setImageBitmap(getUserImage(onlineUserModel.getImage()));
        }
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
