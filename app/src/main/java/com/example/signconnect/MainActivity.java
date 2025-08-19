package com.example.signconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView appNameTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        logoImageView = findViewById(R.id.logoImageView);
        appNameTextView = findViewById(R.id.appNameTextView);

        // Load animation resource (smooth assembly animation)
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_assembly);

        // Start logo animation
        logoImageView.startAnimation(logoAnimation);

        // Set listener for animation events
        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appNameTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appNameTextView.setVisibility(View.VISIBLE);
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in);
                fadeIn.setDuration(800); // fade-in duration
                appNameTextView.startAnimation(fadeIn);

                // After fade-in completes, navigate to the next activity
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        navigateNext();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action needed
            }
        });
    }

    private void navigateNext() {
        if (isUserLoggedIn()) {
            // User is logged in, navigate to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            // User not logged in, navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish(); // Close splash activity
    }

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null; // User is logged in if currentUser is not null
    }
}
