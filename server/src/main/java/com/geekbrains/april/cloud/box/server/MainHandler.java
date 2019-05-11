package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.FileMessage;
import com.geekbrains.april.cloud.box.common.FileMovedRequest;
import com.geekbrains.april.cloud.box.common.FileRequest;
import com.geekbrains.april.cloud.box.common.FolderMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get("server/src/main/resources/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server/src/main/resources/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                    ctx.writeAndFlush(new FolderMessage("server/src/main/resources".substring(0)));//кидаем список файлов сервера на клиент
                }
            }
            if (msg instanceof FileMovedRequest) {
                FileMovedRequest fr = (FileMovedRequest) msg;
                if (Files.exists(Paths.get("server/src/main/resources/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server/src/main/resources/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                    Path movedFile = FileSystems.getDefault().getPath("server/src/main/resources/", fr.getFilename());
                    Files.deleteIfExists(movedFile);
                    ctx.writeAndFlush(new FolderMessage("server/src/main/resources".substring(0)));//кидаем список файлов сервера на клиент
                }
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


}


