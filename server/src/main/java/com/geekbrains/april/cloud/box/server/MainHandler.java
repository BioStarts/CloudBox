package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.FileMessage;
import com.geekbrains.april.cloud.box.common.FileMovedRequest;
import com.geekbrains.april.cloud.box.common.FileRequest;
import com.geekbrains.april.cloud.box.common.FolderMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.*;

public class MainHandler extends ChannelInboundHandlerAdapter {

    public final int BIG_FILE = 100000;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            StringBuilder pathDirectory = new StringBuilder().append("server\\src\\main\\resources\\").append(AuthHandler.getLogin()).append("/");

            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get(pathDirectory + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get(pathDirectory.toString() + fr.getFilename()));

                    try {
                        if (fr.getFilename().length() > 0) {

                            FileInputStream inputStream = new FileInputStream(pathDirectory.toString() + fr.getFilename());
                            System.out.println(pathDirectory.toString() + fr.getFilename() + " path");
                            System.out.println(inputStream.available());
                            int arrByteSize = 8192;
                            byte[] arrbyte = new byte[arrByteSize];
                            int count = 0;
                            while (inputStream.available()>0){
                                System.out.println("читаем файл");
                                if (inputStream.available()<arrByteSize){
                                    System.out.println(" файл меньше чем " + arrByteSize);

                                    inputStream.read(arrbyte, 0, inputStream.available());

                                    count++;
                                    fm.setData(arrbyte);
                                    fm.setCountChunk(count);

                                    //Network.sendMsg(msg);
                                    ctx.writeAndFlush(fm);
                                } else {
                                inputStream.read(arrbyte);
                                count++;
                                fm.setData(arrbyte);
                                fm.setCountChunk(count);

                                //Network.sendMsg(msg);
                                ctx.writeAndFlush(fm);
                                }
                            }


                        }
                    } finally {
                        ReferenceCountUtil.release(msg);
                    }

                    //if (fm.getData().length < BIG_FILE) {//проверяем большой файл или нет
                    //    ctx.writeAndFlush(fm);
                    /*} else {//начинаем принимать большой файл кусками, вычитавыя куски байт
                        int countChunk = (fm.getData().length / BIG_FILE);//считаем количество кусков файла
                        System.out.println("сервер отправляет большой файл");
                        for (int i = 0; i < countChunk; i++) {
                            byte[] bigFileBytes = ByteBuffer.allocate(4).putInt(fm.getData()[i]).array();
                            ctx.writeAndFlush(bigFileBytes);
                            bigFileBytes = null;//обнуляем для записи и отправки следующего куска
                        }
                        System.out.println("сервер отправил большой файл");
                    }*/

                    ctx.writeAndFlush(new FolderMessage(pathDirectory.toString()));//кидаем список файлов сервера на клиент
                }
            }
            if (msg instanceof FileMovedRequest) {
                FileMovedRequest fr = (FileMovedRequest) msg;
                if (Files.exists(Paths.get(pathDirectory + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get(pathDirectory + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                    Path movedFile = FileSystems.getDefault().getPath(pathDirectory.toString(), fr.getFilename());
                    Files.deleteIfExists(movedFile);
                    ctx.writeAndFlush(new FolderMessage(pathDirectory.toString()));//кидаем список файлов сервера на клиент
                }
            }
            if (msg instanceof FileMessage){
                FileMessage fm = (FileMessage) msg;
                System.out.println(pathDirectory.toString()+fm.getFilename());
                if(fm.getCountChunk()==1){
                    Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                } else {
                    Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.APPEND);
                }
                ctx.fireChannelRead(fm);
                ctx.writeAndFlush(fm);
                ctx.writeAndFlush(new FolderMessage(pathDirectory.toString()));//кидаем список файлов сервера на клиент

            }


        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//прием файла на сервер по частям

        StringBuilder pathDirectory = new StringBuilder().append("server\\server_storage\\").append(AuthHandler.getLogin()).append("/");
        if (msg == null) {
            return;
        }
        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;

            if (Files.exists(Paths.get(pathDirectory.toString() + fr.getFilename()))) {

                FileMessage fm = new FileMessage(Paths.get(pathDirectory.toString() + fr.getFilename()));
                ctx.writeAndFlush(fm);

            }
        }
        if (msg instanceof FileMessage){
            FileMessage fm = (FileMessage) msg;
            System.out.println(pathDirectory.toString()+fm.getFilename());
            if(fm.getCountChunk()==1){
                Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
            } else {
                Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.APPEND);
            }
            ctx.fireChannelRead(fm);
            ctx.writeAndFlush(fm);

        }

    }*/


}


