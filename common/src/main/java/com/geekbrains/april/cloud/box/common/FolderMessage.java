package com.geekbrains.april.cloud.box.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FolderMessage extends AbstractMessage {
    private String path;
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public FolderMessage(String path) throws IOException {
        this.path = path;
        this.data = new ArrayList<>();
        Files.list(Paths.get(path)).map(p -> p.getFileName().toString()).forEach(o -> this.data.add(o));
    }
}
