package zyh.smartlibrary;

public class ModelClass {
    private String imageName;
    private String bookAuthor;
    private String bookTitle;

    public ModelClass(String imageName, String bookAuthor, String bookTitle) {
        this.imageName = imageName;
        this.bookAuthor = bookAuthor;
        this.bookTitle = bookTitle;
    }

    public String getImageName() {
        return imageName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
