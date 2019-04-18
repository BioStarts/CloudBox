package com.geekbrains.april.cloud.box.common;

import java.io.Serializable;

public class Message implements Serializable {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message(String text) {
        this.text = text;
    }
}
