package jp.ac.aiit.jointry.models.blocks.procedure.statement;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class Speech extends Statement {

    private TextField tf;

    public Speech() {
        super();
        rect.setFill(getColor());
        getChildren().remove(cb);

        this.tf = new TextField();
        getChildren().add(tf);
    }

    public static Color getColor() {
        return Color.LAVENDER;
    }

    public Label getLabel() {
        return new Label("はなす");
    }

    public String intern() {
        StringBuilder sb = new StringBuilder();
        sb.append("speech").append(" \"").append(tf.getText()).append("\" \n");
        if (nextBlock != null) {
            sb.append(nextBlock.intern());
        }
        return sb.toString();
    }
}
