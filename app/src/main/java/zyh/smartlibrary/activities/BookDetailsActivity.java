package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import zyh.smartlibrary.R;
public class BookDetailsActivity extends AppCompatActivity {
    private ImageView bookImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView typeTextView;
    private TextView descriptionTextView;
    private TextView quantityTextView;
    private Button borrowButton;
    private DatePicker datePicker;
    private Button showDatePickerButton;
    private String studentUid;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookImageView = findViewById(R.id.bookImageView);
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        typeTextView = findViewById(R.id.typeTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        borrowButton = findViewById(R.id.borrowButton);
        datePicker = findViewById(R.id.datePicker);
        showDatePickerButton = findViewById(R.id.showDatePickerButton);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("book")) {
            Book book = (Book) intent.getSerializableExtra("book");
            bookId = intent.getStringExtra("bookId");
            if (book != null) {
                // Populate the views with the book information
                Glide.with(this)
                        .load(book.getImageUrl())
                        .apply(RequestOptions.placeholderOf(R.drawable.michelle_obama))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(bookImageView);

                titleTextView.setText(book.getTitle());
                authorTextView.setText(book.getAuthor());
                typeTextView.setText(book.getType());
                descriptionTextView.setText(book.getDescription());
                quantityTextView.setText(String.valueOf(book.getQuantity()));
            }
        }

        borrowButton.setEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            studentUid = user.getUid();

            borrowButton.setEnabled(true);
            borrowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    Date borrowDate = calendar.getTime();
                    borrowBook(studentUid, bookId, borrowDate);
                    Intent i = new Intent(BookDetailsActivity.this,Books_page.class);
                    startActivity(i);
                    finish();

                }
            });
        } else {
        }

        showDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDatePickerVisibility();
            }
        });
    }

    private void borrowBook(String studentUid, String bookId, Date borrowDate) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference bookRef = dbRef.child("books").child(bookId);
        DatabaseReference studentRef = dbRef.child("students").child(studentUid).child("borrowed_books");

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(bookId)) {
                    Toast.makeText(BookDetailsActivity.this, "You have already borrowed this book.", Toast.LENGTH_SHORT).show();
                } else {
                    bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object quantityObj = dataSnapshot.child("quantity").getValue();
                            if (quantityObj instanceof Long) {
                                long quantity = (Long) quantityObj;
                                if (quantity > 0) {
                                    bookRef.child("quantity").setValue(quantity - 1);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(borrowDate);
                                    calendar.add(Calendar.DATE, 10);
                                    Date returnDate = calendar.getTime();

                                    DatabaseReference borrowRef = dbRef.child("borrows").push();
                                    Map<String, Object> borrowData = new HashMap<>();
                                    borrowData.put("studentUid", studentUid);
                                    borrowData.put("bookId", bookId);
                                    borrowData.put("borrowDate", borrowDate.toString());
                                    borrowData.put("returnDate", returnDate.toString());
                                    borrowRef.setValue(borrowData);

                                    studentRef.child(bookId).setValue(true);
                                    Toast.makeText(BookDetailsActivity.this, "To books borrowed, knowledge explored. Cheers!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BookDetailsActivity.this, "Book is unavailable.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Borrow Book", "Quantity is not a Long");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Borrow Book", "Failed to read book", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Borrow Book", "Failed to read student's borrowed books", databaseError.toException());
            }
        });
    }

    private void toggleDatePickerVisibility() {
        if (datePicker.getVisibility() == View.VISIBLE) {
            datePicker.setVisibility(View.GONE);
        } else {
            datePicker.setVisibility(View.VISIBLE);
        }
    }

}
