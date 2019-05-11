package com.geekbrains.april.cloud.box.client;

import com.geekbrains.april.cloud.box.common.*;
import io.netty.util.ReferenceCountUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private boolean authenticated ;//
    private static StringBuilder pathDirectory = new StringBuilder();//

    @FXML
    HBox filePanel, fileList, authPanel, regPanel;//

    @FXML
    PasswordField passField, passFieldreg;//

    @FXML
    TextField loginField, loginFieldreg;//


    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesList;

    @FXML
    ListView<String> source;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        setAuthenticated(false);//
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\resources\\" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        if(authenticated)refreshLocalFilesList();
                    }
                    if (am instanceof FolderMessage){
                        FolderMessage fm = (FolderMessage) am;
                        refreshServerList(fm);
                    }
                    if (am instanceof FileAuth){
                        FileAuth fileAuth = (FileAuth) am;

                        setAuthenticated(fileAuth.getAuth());
                        refreshLocalFilesList();
                        pathDirectory.append("C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\resources\\").append(fileAuth.getLogin()).append("/");
                        File f = new File(pathDirectory.toString());
                        if(f.mkdir()){
                            System.out.println("создана директория " + fileAuth.getLogin());
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        if(authenticated)refreshLocalFilesList();//
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnMoveBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new FileMovedRequest(tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\resources")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    filesList.getItems().clear();
                    Files.list(Paths.get("C:\\Users\\User\\Geek\\cloud_3\\april-cloud-box\\client\\src\\main\\resources")).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void refreshServerList(FolderMessage msg){
        if (Platform.isFxApplicationThread()){
            source.getItems().clear();
            source.getItems().addAll(msg.getData());
        } else {
            Platform.runLater(() -> {
                source.getItems().clear();
                source.getItems().addAll(msg.getData());
            });
        }
    }

    public void setAuthenticated(boolean auth) {//

        this.authenticated = auth;
        if (authenticated) System.out.println("клиент авторизован" );
        fileList.setVisible(authenticated);
        fileList.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        filePanel.setVisible(authenticated);
        filePanel.setManaged(authenticated);
        source.setVisible(authenticated);
        source.setManaged(authenticated);
        source.setVisible(authenticated);
        source.setManaged(authenticated);
        if (!authenticated) {
            // nickname = "";
        }
    }

    public void sendAuth(ActionEvent actionEvent) throws IOException {

        FileAuth msg = new FileAuth(loginField.getText(), passField.getText());
        msg.setNewUser(false);
        try {
            if (loginField.getText().length()>0 &
                    passField.getText().length()>0) {

                Network.sendMsg(msg);

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
        loginField.clear();
        passField.clear();
    }


    public void sendNewUser(ActionEvent actionEvent) {
        FileAuth msg = new FileAuth(loginField.getText(), passField.getText());

        msg.setNewUser(true);
        try {
            if (loginField.getText().length()>0 &
                    passField.getText().length()>0) {

                Network.sendMsg(msg);

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
        loginField.clear();
        passField.clear();

    }
}
