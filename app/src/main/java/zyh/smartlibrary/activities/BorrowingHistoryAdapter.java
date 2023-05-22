package zyh.smartlibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zyh.smartlibrary.R;

public class BorrowingHistoryAdapter extends RecyclerView.Adapter<BorrowingHistoryAdapter.BorrowingHistoryViewHolder> {

    private List<BorrowingHistoryItem> borrowingHistoryItemList;
    private Context context;

    public BorrowingHistoryAdapter(Context context, List<BorrowingHistoryItem> borrowingHistoryItemList) {
        this.context = context;
        this.borrowingHistoryItemList = borrowingHistoryItemList;
    }

    @NonNull
    @Override
    public BorrowingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrow_item, parent, false);
        return new BorrowingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowingHistoryViewHolder holder, int position) {
        BorrowingHistoryItem item = borrowingHistoryItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return borrowingHistoryItemList.size();
    }

    public class BorrowingHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView borrowDateTextView;

        public BorrowingHistoryViewHolder(View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            borrowDateTextView = itemView.findViewById(R.id.borrowDateTextView);
        }

        public void bind(BorrowingHistoryItem item) {
            bookTitleTextView.setText(item.getBookTitle());


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date borrowDate = item.getBorrowDate();

            if (borrowDate != null) {
                String formattedDate = sdf.format(borrowDate);
                borrowDateTextView.setText(formattedDate);
            } else {
                borrowDateTextView.setText("Date not available");
            }
        }


    }
}
