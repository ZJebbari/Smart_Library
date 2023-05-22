package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import zyh.smartlibrary.R;

public class UpdateUserPage extends AppCompatActivity {

    private ImageView gearOne, gearTwo;
    private EditText emailField, firstNameField, lastNameField, studentIdField, passwordField, passwordConfirmField;
    private String firstName, lastName, email, password,passwordConfirm, studentId;
    private Button edit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_page);

        showUserProfile(firebaseUser);

        gearOne = findViewById(R.id.gearOne);
        gearTwo =findViewById(R.id.gearTwo);

        emailField = findViewById(R.id.emailModificationPage);
        firstNameField = findViewById(R.id.firstNameModificationPage);
        lastNameField = findViewById(R.id.lastNameModificationPage);
        studentIdField = findViewById(R.id.studentIdModificationPage);
        edit = findViewById(R.id.editButtonModificationPage);
        passwordField = findViewById(R.id.passwordModificationPage);
        passwordConfirmField = findViewById(R.id.confirmPasswordModificationPage);
        progressBar = findViewById(R.id.progressBarModificationPage);


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.gear_anim);
        gearOne.setAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.gear_two_anim);
        gearTwo.setAnimation(animation2);

        progressBar.setVisibility(View.INVISIBLE);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {

        if (TextUtils.isEmpty(firstName)){
            Toast.makeText(UpdateUserPage.this,"Please Enter your First Name",Toast.LENGTH_SHORT).show();
            firstNameField.setError("First Name is required");
            firstNameField.requestFocus();
        }
        else if (TextUtils.isEmpty(lastName)){
            Toast.makeText(UpdateUserPage.this,"Please Enter your Last Name",Toast.LENGTH_SHORT).show();
            lastNameField.setError("Last Name is required");
            lastNameField.requestFocus();
        }

        else if (TextUtils.isEmpty(email)){
            Toast.makeText(UpdateUserPage.this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
            emailField.setError("Email is required");
            emailField.requestFocus();
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(UpdateUserPage.this,"Please re-enter your Email",Toast.LENGTH_SHORT).show();
            emailField.setError("Valid email is required");
            emailField.requestFocus();
        }
        else {

            email = emailField.getText().toString();
            firstName = firstNameField.getText().toString();
            lastName = lastNameField.getText().toString();
            studentId = studentIdField.getText().toString();
            password = passwordField.getText().toString();
            passwordConfirm = passwordConfirmField.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(firstName,lastName,studentId);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Students");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            reference.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UpdateUserPage.this,"Update Successful!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateUserPage.this, Welcome_Page.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateUserPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            if (!email.equals(firebaseUser.getEmail())){
                firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            firebaseUser.sendEmailVerification();

                            Toast.makeText(UpdateUserPage.this,"Email has been updated", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(UpdateUserPage.this, Login_Page.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            try {
                                throw task.getException();
                            }catch (Exception e){
                                Toast.makeText(UpdateUserPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)){

                 if (TextUtils.isEmpty(password)){
                    Toast.makeText(UpdateUserPage.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                    passwordField.setError("Password is required");
                    passwordField.requestFocus();
                } else if (TextUtils.isEmpty(passwordConfirm)) {
                    Toast.makeText(UpdateUserPage.this,"Please confirm your password",Toast.LENGTH_SHORT).show();
                    passwordConfirmField.setError("Confirm your Password");
                    passwordConfirmField.requestFocus();
                }
                else if (!password.equals(passwordConfirm)){
                    Toast.makeText(UpdateUserPage.this,"Verify Password",Toast.LENGTH_SHORT).show();
                    passwordField.setError("Passwords don't match");
                    passwordField.requestFocus();
                    passwordField.clearComposingText();
                    passwordConfirmField.clearComposingText();
                }
                else if (password.length()<8) {
                    Toast.makeText(UpdateUserPage.this,"Password should be at least 8 digits",Toast.LENGTH_SHORT).show();
                    passwordField.setError("Password too weak");
                    passwordField.requestFocus();
                }else {
                    firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UpdateUserPage.this,"Password has been changed", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(UpdateUserPage.this,Login_Page.class);
                                startActivity(intent);
                                finish();
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateUserPage.this,e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                 }
                progressBar.setVisibility(View.INVISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Students");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    firstName = readUserDetails.getFirstName();
                    lastName = readUserDetails.getLastName();
                    studentId = readUserDetails.getStudentId();
                    email = firebaseUser.getEmail();

                    firstNameField.setText(firstName);
                    lastNameField.setText(lastName);
                    emailField.setText(email);
                    studentIdField.setText(studentId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateUserPage.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}