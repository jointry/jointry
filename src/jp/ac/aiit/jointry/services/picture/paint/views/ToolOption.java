package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolOption extends ImageView {

    private int penSize;
    private String shape;
    private Image onImage;
    private Image offImage;

    public void setOn() {
        this.setImage(onImage);
    }

    public void setOff() {
        this.setImage(offImage);
    }

    public int getPenSize() {
        return penSize;
    }

    @Deprecated //JavaFXライブラリからのみ使用
    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }

    public String getShape() {
        return shape;
    }

    @Deprecated //JavaFXライブラリからのみ使用
    public void setShape(String shape) {
        this.shape = shape;
    }

    @Deprecated //JavaFXライブラリからのみ使用
    public void setOptionImage(String name) {
        onImage = makeImage(name + "_a");
        offImage = makeImage(name + "_n");

        this.setImage(offImage);
    }

    @Deprecated //JavaFXライブラリからのみ使用
    public String getOptionImage() {
        return null;
    }

    private Image makeImage(String name) {
        return new Image(getClass().getResource("resource/" + name + ".png").toString());
    }
}
