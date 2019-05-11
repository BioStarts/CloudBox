package com.geekbrains.april.cloud.box.common;


public class FileMovedRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileMovedRequest(String filename) {
        this.filename = filename;
    }
}
