package jp.ac.aiit.jointry.models.blocks;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public abstract class Block extends AnchorPane {

    protected double anchorX;
    protected double anchorY;
    public Connector con;

    public Block() {
        setLayoutX(0);
        setLayoutY(0);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
            }
        });

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                toFront();
                anchorX = getLayoutX() - mouseEvent.getSceneX();
                anchorY = getLayoutY() - mouseEvent.getSceneY();
                setCursor(Cursor.MOVE);
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(Cursor.HAND);
                if (con != null) {
                    con.setFill(Color.TRANSPARENT);
                    setConnector(null);
                }
            }
        });

    }

    public void move(double dx, double dy) {
        setLayoutX(dx);
        setLayoutY(dy);
    }

    public double fetchAllHeight() {
        return getHeight();
    }

    public String intern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String blockIntern() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Label getLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConnector(Connector con) {
        this.con = con;
    }
}
