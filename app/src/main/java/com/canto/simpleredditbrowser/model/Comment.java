package com.canto.simpleredditbrowser.model;

import androidx.annotation.NonNull;

public class Comment {

    private String comment;
    private Author author;
    private String updated;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @NonNull
    @Override
    public String toString() {
        return this.comment + "- by : " + this.author;
    }
}
