package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Listeners.UserListener;
import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Constants;
import com.example.chatapp.Utils.PreferenceManager;
import com.example.chatapp.databinding.ActivityUsersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null){
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                    continue;
                                }

                                User user = new User(
                                        queryDocumentSnapshot.getString(Constants.KEY_NAME),
                                        queryDocumentSnapshot.getString(Constants.KEY_IMAGE),
                                        queryDocumentSnapshot.getString(Constants.KEY_USERNAME),
                                        queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN),
                                        queryDocumentSnapshot.getId()
                                );

                                users.add(user);
                            }
                            if (!users.isEmpty()){
                                binding.userRecyclerView.setVisibility(View.VISIBLE);
                                UserAdapter adapter = new UserAdapter(users,UsersActivity.this);
                                binding.userRecyclerView.setAdapter(adapter);
                            }
                            else {
                                showErrorMessage();
                            }
                        }
                        else {
                            showErrorMessage();
                        }
                    }
                });
    }
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}