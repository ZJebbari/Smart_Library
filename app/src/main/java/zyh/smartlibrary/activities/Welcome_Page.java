package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import zyh.smartlibrary.R;

public class Welcome_Page extends AppCompatActivity {
    
    
    
    private FirebaseAuth mAuth;
    private TextView message;
    private String firstName, lastName;
    private ImageView signOut, notification, modification;
    private Button bookPage, addBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        message = findViewById(R.id.textViewWelcomeMessage);
        signOut = findViewById(R.id.signOutWelcomePage);
        modification = findViewById(R.id.editUserWelcomePage);
        bookPage = findViewById(R.id.buttonBookPage);



        //temporary
        addBook = findViewById(R.id.buttonAddBook);

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome_Page.this, UploadBook.class);
                startActivity(intent);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        if (firebaseUser == null){
            Toast.makeText(this
                    , "Something Went Wrong! User's details are not available at the moment"
                    , Toast.LENGTH_SHORT).show();
        }else {
            showUserProfile(firebaseUser);
        }

        Button borrowingHistoryButton = findViewById(R.id.borrowingHistory);
        borrowingHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome_Page.this, BorrowingHistoryActivity.class);
                startActivity(intent);
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Welcome_Page.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        modification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome_Page.this, CustomizeUser.class);
                startActivity(i);
            }
        });

        bookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome_Page.this, Books_page.class);
                startActivity(i);
            }
        });
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
                    message.setText("Welcome, "+firstName+" "+lastName+"!  We're thrilled to have you here." );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Welcome_Page.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
























