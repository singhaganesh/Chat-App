package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.chatapp.Fragments.CallFragment;
import com.example.chatapp.Fragments.ChatFragment;
import com.example.chatapp.Fragments.StatusFragment;
import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private final int RC_NOTIFICATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},RC_NOTIFICATION);
        }

        setListeners();

        addFragment(new ChatFragment(),true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_NOTIFICATION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else {

            }
        }
    }

    private void setListeners() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.optionChat){
                    addFragment(new ChatFragment(),false);
                }
                if (item.getItemId() == R.id.optionStatus){
                    addFragment(new StatusFragment(),false);
                }
                if (item.getItemId() == R.id.optionCall){
                    addFragment(new CallFragment(),false);
                }
                return true;
            }
        });
    }

    private void addFragment(Fragment fragment, boolean flag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (flag){
            transaction.add(R.id.mainFragmentContainer,fragment);
        }else {
            transaction.replace(R.id.mainFragmentContainer,fragment);
        }
        transaction.commit();
    }
}