package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zyh.smartlibrary.R;

public class BorrowingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BorrowingHistoryAdapter adapter;
    private List<BorrowingHistoryItem> borrowList;
    private String studentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowList = new ArrayList<>();
        adapter = new BorrowingHistoryAdapter(this, borrowList);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            studentUid = user.getUid();
            retrieveBorrowHistory();
        } else {
        }
    }

    private void retrieveBorrowHistory() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("borrows");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                borrowList.clear();
                for (DataSnapshot borrowSnapshot : dataSnapshot.getChildren()) {
                    Borrow borrow = borrowSnapshot.getValue(Borrow.class);

                    if (borrow != null && borrow.getStudentUid().equals(studentUid)) {
                        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(borrow.getBookId());
                        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                                Book book = bookSnapshot.getValue(Book.class);
                                if (book != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
                                    Date borrowDate = null;
                                    try {
                                        borrowDate = dateFormat.parse(borrow.getBorrowDate());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    BorrowingHistoryItem historyItem = new BorrowingHistoryItem(book.getTitle(), book.getAuthor(), borrowDate);
                                    borrowList.add(historyItem);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(BorrowingHistoryActivity.this, "Failed to retrieve book", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BorrowingHistoryActivity.this, "Failed to retrieve borrowing history", Toast.LENGTH_SHORT).show();
            }
        });
    }



}

