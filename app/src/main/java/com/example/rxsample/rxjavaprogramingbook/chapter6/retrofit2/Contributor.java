package com.example.rxsample.rxjavaprogramingbook.chapter6.retrofit2;

import androidx.annotation.NonNull;

public class Contributor {
    private String login;
    private String url;
    private int id;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "id:"+id+" url:"+url+" id"+id;
    }
}
