package com.example.signconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, mobileInput, passwordInput;
    private AutoCompleteTextView disabilityDropdown;
    private Button registerButton;
    private TextView loginLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        mobileInput = findViewById(R.id.mobile_no_input);
        passwordInput = findViewById(R.id.password_input);
        disabilityDropdown = findViewById(R.id.disability_dropdown);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        // Setup Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- Setup disability dropdown options ---
        String[] disabilities = {"Blind", "Deaf", "Voiceless", "None"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                disabilities
        );
        disabilityDropdown.setAdapter(adapter);

        // Show dropdown menu when the field is clicked
        disabilityDropdown.setOnClickListener(v -> disabilityDropdown.showDropDown());
        // Also show dropdown when the field gets focus (optional)
        disabilityDropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                disabilityDropdown.showDropDown();
            }
        });

        // Register button click
        registerButton.setOnClickListener(v -> registerUser());

        // Login page link click
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String mobile = mobileInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String disability = disabilityDropdown.getText().toString().trim();

        // ✅ Validation Checks
        if (!isValidName(name)) {
            nameInput.setError("Name must be ≥ 6 letters and contain at least one uppercase letter");
            nameInput.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            emailInput.setError("Email must be a valid @gmail.com address with no uppercase letters");
            emailInput.requestFocus();
            return;
        }

        if (!isValidMobile(mobile)) {
            mobileInput.setError("Mobile number must be exactly 10 digits");
            mobileInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters long");
            passwordInput.requestFocus();
            return;
        }

        if (disability.isEmpty() ||
                !(disability.equals("Blind") || disability.equals("Deaf") ||
                        disability.equals("Voiceless") || disability.equals("None"))) {
            Toast.makeText(this, "Please select a valid disability option", Toast.LENGTH_SHORT).show();
            disabilityDropdown.requestFocus();
            return;
        }

        // ✅ Firebase Authentication - create account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("email", email);
                    userData.put("mobile", mobile);
                    userData.put("disability", disability);

                    // ✅ Save user data to Firestore
                    db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegistrationActivity.this, "Failed to save user data: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrationActivity.this, "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ✅ Validation helper methods
    private boolean isValidName(String name) {
        if (name.length() < 6) return false;
        return Pattern.compile("[A-Z]").matcher(name).find();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && email.endsWith("@gmail.com")
                && email.equals(email.toLowerCase());
    }

    private boolean isValidMobile(String mobile) {
        return mobile.matches("\\d{10}");
    }
}
