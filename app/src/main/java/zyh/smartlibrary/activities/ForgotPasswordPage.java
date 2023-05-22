package zyh.smartlibrary.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import zyh.smartlibrary.R;

public class ForgotPasswordPage extends AppCompatActivity {

    private EditText email;
    private Button send;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        email = findViewById(R.id.emailForgotPassword);
        send = findViewById(R.id.sendForgotPassword);

        auth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPasswordPage.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    email.setError("Email is required");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(ForgotPasswordPage.this,"Please re-enter you email",Toast.LENGTH_LONG).show();
                    email.setError("Valid email is required");
                    email.requestFocus();
                }else{
                auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ForgotPasswordPage.this
                                            , "We send an email to reset your password"
                                            , Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ForgotPasswordPage.this, Login_Page.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    try {
                                        throw task.getException();
                                    }catch(FirebaseAuthInvalidUserException e){
                                        email.setError("User does not exist or is no longer valid. Please register again");
                                        email.requestFocus();
                                    }catch (FirebaseAuthInvalidCredentialsException e){
                                        email.setError("Invalid credential. check and re_enter.");
                                        email.requestFocus();
                                    }catch (Exception e){
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(ForgotPasswordPage.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });}

            }
        });
    }
}