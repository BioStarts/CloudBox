package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.FileAuth;
import com.geekbrains.april.cloud.box.common.FolderMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.IOException;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private static String login;
    private static String nickname;
    private AuthService authService;
    private boolean isAuth = false;



    static String getLogin() {
        return login;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("авторизация");
        authService = new DBAuthServise();
        if (msg == null) {

            return;
        }

        if (msg instanceof FileAuth) {

            FileAuth am = (FileAuth)msg;

            this.login = am.getLogin();
            SQLHandler.connect();
            System.out.println("успешно подключена база");
            if(am.isNewUser() & !SQLHandler.loginIsUnique(am.getLogin())){
                System.out.println("регистрация новго пользователя");
                SQLHandler.setNewUser(am.getLogin(), am.getPassword());

                createDirectory(login);
            }
            nickname = authService.getNicknameByLoginAndPassword(login, am.getPassword());


            if (nickname != null){
                System.out.println("подключён пользователь " + " login - " + login + "  nickname - " + nickname);
                isAuth =true;
                am.setAuth(true);
                ctx.writeAndFlush(am);
                ctx.writeAndFlush(new FolderMessage("server/src/main/resources".substring(0)));//кидаем список файлов сервера на клиент
            }
        }

        if (isAuth){
            ctx.fireChannelRead(msg);
        }


    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void createDirectory(String login) throws IOException {
        StringBuilder pathNewDirectory = new StringBuilder().append("C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\server\\src\\main\\resources\\").append(login).append("/");

        File f = new File(pathNewDirectory.toString());
        if(f.mkdir()){
            System.out.println("create");
        }

    }



}
