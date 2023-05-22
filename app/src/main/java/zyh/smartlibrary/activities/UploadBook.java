package zyh.smartlibrary.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import zyh.smartlibrary.R;

public class UploadBook extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;

    private Uri imageUri;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText typeEditText;
    private EditText quantityEditText;
    private EditText authorEditText;
    private ImageView bookImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        typeEditText = findViewById(R.id.typeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        bookImageView = findViewById(R.id.bookImageView);

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String author = authorEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String type = typeEditText.getText().toString().trim();
                String quantityString = quantityEditText.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                        TextUtils.isEmpty(type) || TextUtils.isEmpty(quantityString) || imageUri == null) {
                    Toast.makeText(UploadBook.this, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                int quantity = Integer.parseInt(quantityString);
                uploadBook(title,author, description, type, quantity, imageUri);
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(UploadBook.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(UploadBook.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        } else {
            openFileChooser();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            bookImageView.setImageURI(imageUri);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else {
                    Toast.makeText(this, "Permission denied to read from storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void uploadBook(String title, String author, String description, String type, int quantity, Uri imageUri) {

        StorageReference fileReference = FirebaseStorage.getInstance().getReference("books").child(title);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        String bookId = FirebaseDatabase.getInstance().getReference("books").push().getKey();


                        Book book = new Book( title, author, description, type, quantity, uri.toString(),System.currentTimeMillis());

                        // Add the book to Realtime Database using the book ID
                        FirebaseDatabase.getInstance().getReference("books").child(bookId).setValue(book)
                                .addOnSuccessListener(aVoid -> Toast.makeText(UploadBook.this, "Book uploaded successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(UploadBook.this, "Failed to upload book", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(UploadBook.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }



}