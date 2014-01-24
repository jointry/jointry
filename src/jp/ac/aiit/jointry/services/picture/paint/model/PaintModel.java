package jp.ac.aiit.jointry.services.picture.paint.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import jp.ac.aiit.jointry.services.picture.paint.views.PaintTool;

public class PaintModel {

    private final SimpleObjectProperty<PaintTool> ptool = new SimpleObjectProperty();
    private final SimpleObjectProperty<Color> color = new SimpleObjectProperty();
    private final SimpleObjectProperty<Image> image = new SimpleObjectProperty();

    public void setPtool(PaintTool ptool) {
        this.ptool.set(ptool);
    }

    public void addPtoolListener(ChangeListener listener) {
        this.ptool.addListener(listener);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color.get();
    }

    public void addColorListener(ChangeListener listener) {
        this.color.addListener(listener);
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public void addImageListener(ChangeListener listener) {
        this.image.addListener(listener);
    }
}
