package com.geekbrains.april.cloud.box.server;

public class DBAuthServise implements AuthService{
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean changeNick(String nickname, String newNickname) {
        return SQLHandler.changeNick(nickname, newNickname);
    }
}

