package zyh.smartlibrary.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import zyh.smartlibrary.R;

public class DeleteAccount extends AppCompatActivity {

    private ImageView deleteProfileIcon;
    private Button delete, authenticate;
    private EditText  password;
    private TextView text;
    private String userPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        deleteProfileIcon = findViewById(R.id.deleteprofileIcon);

        delete = findViewById(R.id.deleteProfileButton);
        authenticate = findViewById(R.id.authenticateDeleteProfileButton);
        text = findViewById(R.id.textDeleteProfile);
        password = findViewById(R.id.deleteProfilePassword);


        delete.setEnabled(false);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(DeleteAccount.this,"Something went wrong! User details are not available at the moment"
                    ,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DeleteAccount.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.delete_profile_anim);
        deleteProfileIcon.setAnimation(animation);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = password.getText().toString();
                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(DeleteAccount.this,"Password is needed",Toast.LENGTH_LONG).show();
                    password.setError("Please entre your current password to authenticate");
                    password.requestFocus();
                }else{
                    //ReAuthenticate User now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                password.setEnabled(false);

                                authenticate.setEnabled(false);
                                delete.setEnabled(true);


                                text.setText("You are authenticated/verified. You can delete profile now");

                                Toast.makeText(DeleteAccount.this, "Password has been verified " +
                                        "You can delete your profile now. Be careful, this action is irreversible",
                                        Toast.LENGTH_SHORT).show();


                                delete.setBackgroundTintList(ContextCompat.getColorStateList(DeleteAccount.this,
                                        R.color.dark_green));

                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(DeleteAccount.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccount.this);
        builder.setTitle("Delete User and related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible");


        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser(firebaseUser);
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });


        alertDialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    deleteUserData();
                    mAuth.signOut();
                    Toast.makeText(DeleteAccount.this,"User has been deleted!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteAccount.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(DeleteAccount.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteUserData() {
        //Delete data from realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Students");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess:User Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"OnSuccess:User Data Deleted");
                Toast.makeText(DeleteAccount.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}















