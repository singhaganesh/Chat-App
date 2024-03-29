package com.example.chatapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.Listeners.ConversionListener;
import com.example.chatapp.Models.ChatMessageModel;
import com.example.chatapp.Models.User;
import com.example.chatapp.databinding.ItemContainerRecentConversionBinding;
import java.util.List;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionViewHolder> {

    private final List<ChatMessageModel> chatMessageModels;
    private final ConversionListener conversionListener;

    public RecentConversionAdapter(List<ChatMessageModel> chatMessageModels,ConversionListener conversionListener) {
        this.conversionListener = conversionListener;
        this.chatMessageModels = chatMessageModels;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessageModels.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConversionBinding binding;
        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }


        void setData(ChatMessageModel chatMessageModel){

            binding.imageProfile.setImageBitmap(getConversionImage(chatMessageModel.conversionImage));
            binding.textName.setText(chatMessageModel.conversionName);
            binding.textRecentMessage.setText(chatMessageModel.message);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = new User();
                    user.setId(chatMessageModel.conversionId);
                    user.setName(chatMessageModel.conversionName);
                    user.setImage(chatMessageModel.conversionImage);
                    conversionListener.onConversionClicked(user);
                }
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
