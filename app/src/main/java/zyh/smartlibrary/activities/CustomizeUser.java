package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import zyh.smartlibrary.R;

public class CustomizeUser extends AppCompatActivity {

    private Button modification,notification,delete;
    private ImageView logout, gearOne, gearTwo ;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_user);
        modification = findViewById(R.id.accountCustomizePage);
        delete = findViewById(R.id.deleteAccountCustomizePage);
        logout = findViewById(R.id.logoutCustomizePage);
        gearOne = findViewById(R.id.gearOneCustomization);
        gearTwo = findViewById(R.id.gearTwoCustomization);


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.gear_anim);
        gearOne.setAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.gear_two_anim);
        gearTwo.setAnimation(animation2);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(CustomizeUser.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        modification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomizeUser.this, UpdateUserPage.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomizeUser.this, DeleteAccount.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomizeUser.this);
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
                    mAuth.signOut();
                    Toast.makeText(CustomizeUser.this,"User has been deleted!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CustomizeUser.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(CustomizeUser.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}