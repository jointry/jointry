package jp.ac.aiit.jointry.models;

import javafx.scene.image.Image;
import jp.ac.aiit.jointry.controllers.CostumeCntroller;

public class Costume {

    private int number;
    private String title;
    private Image image;

    public Costume(int number, String title, Image image) {
        this.number = number;
        this.title = title;
        this.image = image;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
