package com.geekbrains.april.cloud.box.common;

public class FileAuth extends AbstractMessage {
    private String login;
    private String password;
    private boolean isAuth;
    private boolean newUser;

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public String getLogin() {
        return login;
    }

    public boolean getAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public FileAuth(String login, String password) {
        this.newUser = false;
        this.isAuth = false;
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
