package com.example.meuapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 segundos
    
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView taglineTextView;
    private View backgroundGradient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Inicializar views
        logoImageView = findViewById(R.id.logoImageView);
        appNameTextView = findViewById(R.id.appNameTextView);
        taglineTextView = findViewById(R.id.taglineTextView);
        backgroundGradient = findViewById(R.id.gradientView);
        
        // Iniciar animações
        startAnimations();
        
        // Navegar para MainActivity após o delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // Adicionar transição suave
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }, SPLASH_DURATION);
    }
    
    private void startAnimations() {
        // Animação do logo - bounce in
        ObjectAnimator scaleXLogo = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleYLogo = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator alphaLogo = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f);
        ObjectAnimator rotationLogo = ObjectAnimator.ofFloat(logoImageView, "rotation", -180f, 0f);

        AnimatorSet logoAnimatorSet = new AnimatorSet();
        logoAnimatorSet.playTogether(scaleXLogo, scaleYLogo, alphaLogo, rotationLogo);
        logoAnimatorSet.setDuration(1200);
        logoAnimatorSet.setInterpolator(new BounceInterpolator());
        logoAnimatorSet.start();

        // Animação do nome do app - slide in from left
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator slideInName = ObjectAnimator.ofFloat(appNameTextView, "translationX", -500f, 0f);
            ObjectAnimator alphaName = ObjectAnimator.ofFloat(appNameTextView, "alpha", 0f, 1f);
            ObjectAnimator scaleName = ObjectAnimator.ofFloat(appNameTextView, "scaleX", 0.5f, 1f);

            AnimatorSet nameAnimatorSet = new AnimatorSet();
            nameAnimatorSet.playTogether(slideInName, alphaName, scaleName);
            nameAnimatorSet.setDuration(800);
            nameAnimatorSet.setInterpolator(new OvershootInterpolator());
            nameAnimatorSet.start();
        }, 600);

        // Animação da tagline - fade in with slide up
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator slideUpTagline = ObjectAnimator.ofFloat(taglineTextView, "translationY", 100f, 0f);
            ObjectAnimator alphaTagline = ObjectAnimator.ofFloat(taglineTextView, "alpha", 0f, 1f);

            AnimatorSet taglineAnimatorSet = new AnimatorSet();
            taglineAnimatorSet.playTogether(slideUpTagline, alphaTagline);
            taglineAnimatorSet.setDuration(600);
            taglineAnimatorSet.setInterpolator(new DecelerateInterpolator());
            taglineAnimatorSet.start();
        }, 1200);
    }
}