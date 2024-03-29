package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Listeners.UserListener;
import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Constants;
import com.example.chatapp.Utils.PreferenceManager;
import com.example.chatapp.databinding.ActivitySearchUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity implements UserListener {

    ActivitySearchUserBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }
    private void setListeners() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    binding.searchRecyclerView.setVisibility(View.GONE);
                }else {
                    getUsers(newText);
                }
                return true;
            }
        });
    }

    private void getUsers(String filterText) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereGreaterThanOrEqualTo(Constants.KEY_USERNAME, filterText.toLowerCase().trim())
                .whereLessThanOrEqualTo(Constants.KEY_USERNAME, filterText.toLowerCase().trim() + "\uf8ff")
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
                                showErrorMessage(false);
                                UserAdapter adapter = new UserAdapter(users,SearchUserActivity.this);
                                binding.searchRecyclerView.setAdapter(adapter);
                            }
                            else {
                                showErrorMessage(true);
                            }
                        }
                        else {
                            showErrorMessage(true);
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
    private void showErrorMessage(boolean isError){
        if (isError){
            binding.textErrorMessage.setText(String.format("%s","No user available"));
            binding.textErrorMessage.setVisibility(View.VISIBLE);
            binding.searchRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.textErrorMessage.setVisibility(View.GONE);
            binding.searchRecyclerView.setVisibility(View.VISIBLE);
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