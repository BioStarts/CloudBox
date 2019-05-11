package com.geekbrains.april.cloud.box.server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;

    private static PreparedStatement psGetNickByLoginAndPassword;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psInsertNewUser;
    private static PreparedStatement psLoginIsUnique;


    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:clientBase.db");
            psInsertNewUser = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?); )");
            psChangeNick = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?;");
            psGetNickByLoginAndPassword = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
            psLoginIsUnique =connection.prepareStatement("SELECT login FROM users WHERE login = ?;");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            System.out.println("login   " + login+ " pass " + password);

            psGetNickByLoginAndPassword.setString(1, login);
            psGetNickByLoginAndPassword.setString(2, password);

            ResultSet rs = psGetNickByLoginAndPassword.executeQuery();


            if (rs.next()) {

                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }

    public static boolean changeNick(String oldNickname, String newNickname) {
        try {
            psChangeNick.setString(1, newNickname);
            psChangeNick.setString(2, oldNickname);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static void setNewUser(String login, String password) {
        String nickname = login + "_new_User";
        try{
            System.out.println("новый пользователь:  логин - " + login + " пароль -  " + password);

            psInsertNewUser.setString(1, login);
            psInsertNewUser.setString(2, password);
            psInsertNewUser.setString(3, nickname);
            psInsertNewUser.executeUpdate();
            System.out.println("Зарегестрирован");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean loginIsUnique (String login) throws SQLException {
        boolean callincident = false;

        psLoginIsUnique.setString(1, login);
        ResultSet rs = psLoginIsUnique.executeQuery();

        if (rs.next()) {
            if (login.equals(rs.getString(1))){
                System.out.println("уже есть такой пользователь");
                callincident = true;

            }

        }
        rs.close();

        return callincident;

    }


    public static void disconnect() {

        try {
            psGetNickByLoginAndPassword.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psChangeNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}