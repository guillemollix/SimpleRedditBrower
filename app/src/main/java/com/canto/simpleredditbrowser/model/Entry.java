package com.canto.simpleredditbrowser.model;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;

public class Entry {

    Author author;
    String title;
    String updated;
    String link;
    String thumbnail;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
