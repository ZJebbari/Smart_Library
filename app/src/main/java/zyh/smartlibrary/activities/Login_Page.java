package zyh.smartlibrary.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import zyh.smartlibrary.R;

public class Login_Page extends AppCompatActivity {

    EditText mail, password;
    Button signIn;
    TextView forgotPassword;
    ProgressBar progressBar;
    ImageView imageViewShowHidePwd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mail = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        signIn = findViewById(R.id.buttonLoginSignin);
        forgotPassword = findViewById(R.id.textViewForgotPassword);
        progressBar = findViewById(R.id.progressBarLogin);


        progressBar.setVisibility(View.INVISIBLE);


        mAuth = FirebaseAuth.getInstance();

        // Show Hide Password using Eye Icon
        imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);

        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if password is visible then hide it
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change Icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = mail.getText().toString();
                String userPassword = password.getText().toString();
                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(Login_Page.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    mail.setError("Email is required");
                    mail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(Login_Page.this,"Please re-enter you email",Toast.LENGTH_LONG).show();
                    mail.setError("Valid email is required");
                    mail.requestFocus();
                } else if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(Login_Page.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    password.setError("password is required");
                    password.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(userEmail,userPassword);
                }

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Page.this, ForgotPasswordPage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loginUser(String userEmail, String userPassword) {
        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();



                    if (firebaseUser.isEmailVerified()){

                        Toast.makeText(Login_Page.this,"You are logged in now",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Login_Page.this, Welcome_Page.class);
                        startActivity(intent);
                        finish(); // to close Login Activity
                    }
                    else {
                        firebaseUser.sendEmailVerification();
                        mAuth.signOut();// sign out user
                        showAlertDialog();
                    }
                }else {
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        mail.setError("User does not exist or is no longer valid. Please register again");
                        mail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        mail.setError("Invalid credential. check and re_enter.");
                        mail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login_Page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Login_Page.this);
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
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            Toast.makeText(Login_Page.this,"Already Logged In!",Toast.LENGTH_LONG).show();


            Intent intent = new Intent(Login_Page.this,Welcome_Page.class);
            startActivity(intent);
            finish(); // to close Login Activity
        }else {
            Toast.makeText(Login_Page.this,"You can login now!",Toast.LENGTH_LONG).show();
        }
    }
}