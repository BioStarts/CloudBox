package com.geekbrains.april.cloud.box.server;

public interface AuthService {
    String getNicknameByLoginAndPassword (String login, String password);
    boolean changeNick (String nickname, String newNickname);
}
