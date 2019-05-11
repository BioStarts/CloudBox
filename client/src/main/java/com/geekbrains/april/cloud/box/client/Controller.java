package com.geekbrains.april.cloud.box.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button button;

    @FXML
    ListView<String> source;
    @FXML
    ListView<String> destination;

    @FXML
    public void initialize() throws IOException {
        viewDestination();
        viewSource();
    }

    String sourseWay = "C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\resources";
    String destinationWay = "C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\java\\com\\geekbrains\\april\\cloud\\box\\client";
    String fileMoveName = "\\file.txt";

    public void onClickMethod(ActionEvent actionEvent) throws IOException {
        try {
            Path sourcePath = Paths.get(sourseWay + fileMoveName),
                    destinationPath = Paths.get(destinationWay + fileMoveName);
            Files.move(sourcePath,destinationPath, StandardCopyOption.REPLACE_EXISTING);//перемещаем файл
        } catch (IOException e) {
            e.printStackTrace();
        }
        button.setText("Thanks!");

        viewSource();
        viewDestination();

    }

    public void viewSource () throws IOException {

        source.getItems().clear();

        Files.walkFileTree(Paths.get(sourseWay), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                source.getItems().add(String.valueOf(file.getFileName()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void viewDestination () throws IOException {

        destination.getItems().clear();

        Files.walkFileTree(Paths.get(destinationWay), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                destination.getItems().add(String.valueOf(file.getFileName()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //запускаем соединение
        Network.start();
        /*Thread t = new Thread(() -> {
            while (true){
                try {
                    byte[] am = Network.readObject();//заменил абстрактмсдж на Files.nio
                    Files.write(Paths.get(destinationWay),am,StandardOpenOption.CREATE);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();*/
        try {
            viewSource();
            viewDestination();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickDownload(ActionEvent actionEvent) {

        //Network.sendMsg(new Files());
    }
}
