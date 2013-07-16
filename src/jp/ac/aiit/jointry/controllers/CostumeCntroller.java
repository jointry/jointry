package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CostumeCntroller implements Initializable {

    @FXML
    private ImageView images;
    @FXML
    private TextField title;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInfo(String title, Image image) {
        this.title.setText(title);
        images.setImage(image);
    }
}
