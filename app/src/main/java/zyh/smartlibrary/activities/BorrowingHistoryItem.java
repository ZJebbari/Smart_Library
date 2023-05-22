package zyh.smartlibrary.activities;

import java.util.Date;

import java.util.Date;

public class BorrowingHistoryItem {
    private String bookTitle;
    private String bookAuthor;
    private Date borrowDate;

    public BorrowingHistoryItem(String bookTitle, String bookAuthor, Date borrowDate) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.borrowDate = borrowDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }
}


