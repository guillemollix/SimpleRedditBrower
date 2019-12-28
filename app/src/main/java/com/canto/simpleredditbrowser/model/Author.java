package com.canto.simpleredditbrowser.model;

import androidx.annotation.NonNull;

public class Author {

    String name;
    String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
