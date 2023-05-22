package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import zyh.smartlibrary.R;
import zyh.smartlibrary.adapter.BookAdapter;
public class Books_page extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private EditText editTextTitleOrAuthor;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_page);

        editTextTitleOrAuthor = findViewById(R.id.editTextTitleOrAuthor);
        spinner = findViewById(R.id.spinner);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedType = spinner.getSelectedItem().toString();
                String searchText = editTextTitleOrAuthor.getText().toString().trim();

                retrieveBooks(selectedType, searchText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        editTextTitleOrAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String selectedType = spinner.getSelectedItem().toString();
                String searchText = charSequence.toString().trim();

                retrieveBooks(selectedType, searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void retrieveBooks(final String type, final String searchText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("books");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                boolean isBooksFound = false;

                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);

                    if (book != null) {
                        book.setId(bookSnapshot.getKey());


                        boolean isTypeMatched = type.equals("Select Book Category") || book.getType().equals(type);
                        boolean isTitleOrAuthorMatched = book.getTitle().toLowerCase().contains(searchText.toLowerCase())
                                || book.getAuthor().toLowerCase().contains(searchText.toLowerCase());

                        if (isTypeMatched && isTitleOrAuthorMatched) {
                            bookList.add(book);
                            isBooksFound = true;
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                if (!isBooksFound) {
                    Toast.makeText(Books_page.this, "Sorry, we don't have that category in our library. Please check later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Books_page.this, "Failed to retrieve books", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
