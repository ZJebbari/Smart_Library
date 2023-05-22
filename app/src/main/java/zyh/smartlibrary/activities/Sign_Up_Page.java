package zyh.smartlibrary.activities;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import zyh.smartlibrary.R;

public class Sign_Up_Page extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, studentIdEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox checkBox;
    private Button signUpButton;
    private ProgressBar progressBar;
    private ImageView imageViewShowHidePwd, imageViewShowHideConfirmPwd;

    private DatabaseReference mDatabase  = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean matchFound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        firstNameEditText = findViewById(R.id.firstNameModificationPage);
        lastNameEditText = findViewById(R.id.lastNameModificationPage);
        studentIdEditText = findViewById(R.id.editTextStudentId);
        emailEditText = findViewById(R.id.emailModificationPage);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        checkBox = findViewById(R.id.checkbox);
        signUpButton = findViewById(R.id.editButtonModificationPage);
        progressBar = findViewById(R.id.progressBarModificationPage);

        progressBar.setVisibility(View.INVISIBLE);


        imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd_signUp);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);

        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){

                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });


        imageViewShowHideConfirmPwd = findViewById(R.id.imageView_show_hide_confirm_pwd_signUp);
        imageViewShowHideConfirmPwd.setImageResource(R.drawable.ic_hide_pwd);

        imageViewShowHideConfirmPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmPasswordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){

                    confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    imageViewShowHideConfirmPwd.setImageResource(R.drawable.ic_hide_pwd);
                }else {
                    confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHideConfirmPwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent intent = new Intent(Sign_Up_Page.this, Policy_Page.class);
                    startActivity(intent);
                }
                checkBox.setClickable(false);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent intent = new Intent(Sign_Up_Page.this, Policy_Page.class);
                    startActivity(intent);
                }
                checkBox.setClickable(false);
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract the input values from the views
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String studentId = studentIdEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                boolean checkBoxChecked = checkBox.isChecked();

//                createRandomStudentIds();
//                retrieveStudentIdsFromDatabase(studentId);
                createRandomStudentIdsInDatabase();

                if (TextUtils.isEmpty(firstName)){
                    Toast.makeText(Sign_Up_Page.this,"Please Enter your First Name",Toast.LENGTH_SHORT).show();
                    firstNameEditText.setError("First Name is required");
                    firstNameEditText.requestFocus();
                }
                else if (TextUtils.isEmpty(lastName)){
                    Toast.makeText(Sign_Up_Page.this,"Please Enter your Last Name",Toast.LENGTH_SHORT).show();
                    lastNameEditText.setError("Last Name is required");
                    lastNameEditText.requestFocus();
                }
                else if (TextUtils.isEmpty(studentId)){
                    Toast.makeText(Sign_Up_Page.this,"Please Enter your Student ID",Toast.LENGTH_SHORT).show();
                    studentIdEditText.setError("Student ID is required");
                    studentIdEditText.requestFocus();
                }
                else if (TextUtils.isEmpty(email)){
                    Toast.makeText(Sign_Up_Page.this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Sign_Up_Page.this,"Please re-enter your Email",Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Valid email is required");
                    emailEditText.requestFocus();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Sign_Up_Page.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Sign_Up_Page.this,"Please confirm your password",Toast.LENGTH_SHORT).show();
                    confirmPasswordEditText.setError("Confirm your Password");
                    confirmPasswordEditText.requestFocus();
                }
                else if (!password.equals(confirmPassword)){
                    Toast.makeText(Sign_Up_Page.this,"Verify Password",Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Passwords don't match");
                    passwordEditText.requestFocus();
                    passwordEditText.clearComposingText();
                    confirmPasswordEditText.clearComposingText();
                }
                else if (!checkBoxChecked) {
                    Toast.makeText(Sign_Up_Page.this,"Please read our policy",Toast.LENGTH_SHORT).show();
                } else if (password.length()<8) {
                    Toast.makeText(Sign_Up_Page.this,"Password should be at least 8 digits",Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Password too weak");
                    passwordEditText.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    retrieveRegisterStudentIdsFromDatabase(firstName,  lastName, studentId, email,  password);


                }


            }
        });
    }

    private void retrieveStudentIdsFromDatabase(String firstName, String lastName, String studentId, String email, String password) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("studentsId");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                    if (user != null && user.getStudentId().equals(studentId)) {

                        matchFound = true;
                        break;
                    }
                }


                if (matchFound) {
                    registerUser(firstName, lastName, studentId,  email,  password);
                } else {

                    Toast.makeText(getApplicationContext(), "Student number is not valid", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerUser(String firstName, String lastName, String studentId, String email, String password) {


            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Sign_Up_Page.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();


                                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(firstName, lastName, studentId);


                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Students");


                                referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            Toast.makeText(Sign_Up_Page.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();
                                            // Send Verification Email
//                                            firebaseUser.sendEmailVerification();
//
//                                            //Open welcome page user
//                                            Intent intent = new Intent(Sign_Up_Page.this,Login_Page.class);
//                                            //To prevent user from returning back to Register Activity on pressing back button after registration
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                            finish(); // to close Register Activity

                                            if (firebaseUser.isEmailVerified()){
                                                Toast.makeText(Sign_Up_Page.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();
                                                //open user profile
                                                //Open welcome page user
                                                Intent intent = new Intent(Sign_Up_Page.this, Welcome_Page.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                mAuth.signOut();// sign out user
                                                Intent intent = new Intent(Sign_Up_Page.this, Login_Page.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(Sign_Up_Page.this, "Registration has failed", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    passwordEditText.setError("Your password too weak");
                                    passwordEditText.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    emailEditText.setError("Your email is invalid or already im use");
                                    emailEditText.requestFocus();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    emailEditText.setError("User is already registered with this email");
                                    emailEditText.requestFocus();
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(Sign_Up_Page.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }

                        }


                    });

    }

    private void retrieveRegisterStudentIdsFromDatabase(String firstName, String lastName, String studentId, String email, String password) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Students");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                    if (user != null && user.getStudentId().equals(studentId)) {

                        matchFound = true;
                        break;
                    }
                }


                if (matchFound) {

                    Toast.makeText(getApplicationContext(), "This number is already register", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {

                    retrieveStudentIdsFromDatabase( firstName,  lastName,  studentId,  email,  password);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Sign_Up_Page.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void createRandomStudentIdsInDatabase() {
        DatabaseReference studentIdsRef = FirebaseDatabase.getInstance().getReference("studentIds");

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int randomId = 10000 + random.nextInt(90000);  // Generate a random number between 10000 and 99999
            String studentId = String.valueOf(randomId);
            studentIdsRef.push().setValue(studentId);
        }
    }
}