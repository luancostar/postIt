package com.example.meuapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private TextView textViewCounter;
    private ImageButton btnClose;
    private ImageGalleryAdapter adapter;
    private List<String> imageUris;
    private int currentPosition;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        
        // Ocultar a barra de status para experiência imersiva
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        initViews();
        setupData();
        setupViewPager();
        setupListeners();
    }
    
    private void initViews() {
        viewPager = findViewById(R.id.viewPagerGallery);
        textViewCounter = findViewById(R.id.textViewCounter);
        btnClose = findViewById(R.id.btnClose);
    }
    
    private void setupData() {
        Intent intent = getIntent();
        imageUris = intent.getStringArrayListExtra("image_uris");
        currentPosition = intent.getIntExtra("current_position", 0);
        
        if (imageUris == null) {
            imageUris = new ArrayList<>();
        }
    }
    
    private void setupViewPager() {
        adapter = new ImageGalleryAdapter(this, imageUris);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);
        
        updateCounter(currentPosition);
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                updateCounter(position);
            }
        });
    }
    
    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }
    
    private void updateCounter(int position) {
        if (imageUris.size() > 1) {
            textViewCounter.setText((position + 1) + " de " + imageUris.size());
            textViewCounter.setVisibility(View.VISIBLE);
        } else {
            textViewCounter.setVisibility(View.GONE);
        }
    }
}