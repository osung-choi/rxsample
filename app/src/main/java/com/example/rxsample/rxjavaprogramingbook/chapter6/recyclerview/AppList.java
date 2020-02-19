package com.example.rxsample.rxjavaprogramingbook.chapter6.recyclerview;

import android.graphics.drawable.Drawable;

public class AppList {
    private Drawable image;
    private String title;

    public AppList(Drawable image, String title) {
        this.image = image;
        this.title = title;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
