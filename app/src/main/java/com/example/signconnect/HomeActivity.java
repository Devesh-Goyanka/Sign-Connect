package com.example.signconnect;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private MaterialToolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userName = "User";
    private String userImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setDrawerWidth();

        ImageView menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ImageView profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> {
        });

        // Card click handlers
        findViewById(R.id.card_gesture_translator).setOnClickListener(v -> {
        });
        findViewById(R.id.card_learn_sign_language).setOnClickListener(v -> {
        });
        findViewById(R.id.card_chat_fragment).setOnClickListener(v -> {
        });

        navView.setNavigationItemSelectedListener(this::onMenuItemSelected);

        updateNavHeaderFromFirebase();
    }

    private void setDrawerWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int drawerWidth = (int) (size.x * 0.7);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navView.getLayoutParams();
        params.width = drawerWidth;
        navView.setLayoutParams(params);
    }


    private boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START, true);

        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_learn) {
        } else if (id == R.id.nav_chat) {
        } else if (id == R.id.nav_library) {
        } else if (id == R.id.nav_settings) {
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
        return true;
    }


    /**
     * Fetch user name and photo URL from Firebase Firestore and Auth and update navigation header
     */
    private void updateNavHeaderFromFirebase() {
        View header = navView.getHeaderView(0);
        TextView navUserName = header.findViewById(R.id.user_name);
        ImageView navUserImage = header.findViewById(R.id.profile_image);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        navUserName.setText("User");

        String uid = user.getUid();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.getString("name");
                String imageUrl = snapshot.getString("imageUrl"); // Optional: store photo in Firestore user doc
                if (name != null && !name.isEmpty()) navUserName.setText(name);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(navUserImage);
                } else if (user.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(navUserImage);
                }
            }
        }).addOnFailureListener(e -> {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_profile)
                        .circleCrop()
                        .into(navUserImage);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
