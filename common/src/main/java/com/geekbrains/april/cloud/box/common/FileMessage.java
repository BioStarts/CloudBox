package com.geekbrains.april.cloud.box.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String filename;

    private byte[] data;

    public boolean fileSize;  // = 1 если большой файл и 0 если маленький

    public int countChunk; // количество кусков при отправке большого файла
    public boolean isFileSize() {
        return fileSize;
    }

    public void setFileSize(boolean fileSize) {
        this.fileSize = fileSize;
    }

    public int getCountChunk() {
        return countChunk;
    }

    public void setCountChunk(int countChunk) {
        this.countChunk = countChunk;
    }

    public String getFilename() {
        return filename;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    /*public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }*/
    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = getData();
    }
}

