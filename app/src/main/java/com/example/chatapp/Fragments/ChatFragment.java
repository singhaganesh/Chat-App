package com.example.chatapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.chatapp.Adapters.OnlineUserAdapter;
import com.example.chatapp.Adapters.RecentConversionAdapter;
import com.example.chatapp.Listeners.ConversionListener;
import com.example.chatapp.Models.ChatMessageModel;
import com.example.chatapp.Models.OnlineUserModel;
import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Constants;
import com.example.chatapp.Utils.PreferenceManager;
import com.example.chatapp.activities.ChatActivity;
import com.example.chatapp.activities.SearchUserActivity;
import com.example.chatapp.activities.SignInActivity;
import com.example.chatapp.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment implements ConversionListener {
    private PreferenceManager preferenceManager;
    private FragmentChatBinding binding;
    private List<ChatMessageModel> conversions;
    private RecentConversionAdapter adapter;
    private OnlineUserAdapter onlineUserAdapter;
    private FirebaseFirestore database;
    private List<OnlineUserModel> onlineUserModels;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater,container,false);


        preferenceManager = new PreferenceManager(requireContext());
        inIt();
        setListeners();
        loadUserDetails();
        getToken();
        listenOnlineUser();
        listenConversations();

        return binding.getRoot();
    }


    private void listenOnlineUser() {
        loading(true);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_AVAILABILITY,1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            onlineUserModels = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : task.getResult()){

                                String image = documentSnapshot.getString(Constants.KEY_IMAGE);
                                String userName = documentSnapshot.getString(Constants.KEY_USERNAME);
                                if (image != null && userName!=null){
                                    OnlineUserModel onlineUserModel = new OnlineUserModel(image,userName);
                                    onlineUserModels.add(onlineUserModel);
                                }
                            }
                            loading(false);
                            onlineUserAdapter = new OnlineUserAdapter(onlineUserModels);
                            binding.onlineStatusRecyclerView.setAdapter(onlineUserAdapter);


                        }
                    }
                });
    }

    private void inIt(){
        conversions = new ArrayList<>();
        adapter = new RecentConversionAdapter(conversions, ChatFragment.this);
        binding.conversionsRecyclerView.setAdapter(adapter);


        database = FirebaseFirestore.getInstance();
    }
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        binding.chatSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchUserActivity.class));
            }
        });

    }
    private void signOut(){
        showToast("Signing out... ");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String,Object> update = new HashMap<>();
        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clear();
                        startActivity(new Intent(requireContext(), SignInActivity.class));
                        requireActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to sing out");
                    }
                });
    }
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CONVERSIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null){
                return;
            }
            if (value != null){
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        ChatMessageModel chatMessageModel = new ChatMessageModel();
                        chatMessageModel.senderId = senderId;
                        chatMessageModel.receiverId = receiverId;
                        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                            chatMessageModel.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                            chatMessageModel.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                            chatMessageModel.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        }else {
                            chatMessageModel.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                            chatMessageModel.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                            chatMessageModel.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        }
                        chatMessageModel.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                        chatMessageModel.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                        conversions.add(chatMessageModel);
                    }
                        else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                            for (int i = 0;i<conversions.size();i++){
                                String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                                String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                                if (conversions.get(i).senderId.equals(senderId) && conversions.get(i).receiverId.equals(receiverId)){
                                    conversions.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                                    conversions.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                                    break;
                                }
                            }
                        }
                    }
                Collections.sort(conversions, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
                adapter.notifyDataSetChanged();
                binding.conversionsRecyclerView.smoothScrollToPosition(0);
                binding.conversionsRecyclerView.setVisibility(View.VISIBLE);
                binding.ProgressBar.setVisibility(View.GONE);
                }
            }
    };
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }


    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to update token");
                    }
                });
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        requireActivity().finish();
    }
    private void loading(boolean load){
        if (load){
            binding.onlineStatusRecyclerView.setVisibility(View.GONE);
            binding.progressBarOnlineUser.setVisibility(View.VISIBLE);
        }
        else {
            binding.onlineStatusRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBarOnlineUser.setVisibility(View.GONE);
        }
    }
}